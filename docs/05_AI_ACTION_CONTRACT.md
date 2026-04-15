# 05_AI_ACTION_CONTRACT.md

## Purpose
Define the strict structured action contract returned by AI for all interpretation and summary flows.

## Core rule
AI must return strict JSON conforming to one of the supported action types below.
Free-form prose alone is never sufficient for state-changing flows.
Unknown extra fields are ignored.
Missing required fields force fallback to UNKNOWN action type.
Completely malformed or unparseable responses are dropped. A pre-canned failure message is shown to the user.

## Contract version
contractVersion: 1

## Supported action types
- DERIVE_ONBOARDING_GUIDANCE
- LOG_MEAL
- ASK_CLARIFICATION
- SHOW_DAILY_SUMMARY
- SHOW_WEEKLY_SUMMARY
- SHOW_MONTHLY_SUMMARY
- SUGGEST_FOOD_IMPROVEMENTS
- EDIT_EXISTING_MEAL
- CARD_DRAFT_MEAL
- CARD_STRESS_TEST_RESULT
- UPDATE_FOOD_REFERENCE
- UNKNOWN

---

## Common envelope
Every response must include these fields regardless of action type:

```kotlin
val contractVersion: Int          // required, must match current version
val actionType: String            // required, must match a supported action type
val confidence: Float             // required, 0.0–1.0
val rationale: String             // required, brief internal explanation of assumptions
val requiresReview: Boolean       // required
```

Silent correction rules for envelope fields:
- confidence < 0.0 → clamped to 0.0
- confidence > 1.0 → clamped to 1.0
- missing contractVersion → treated as malformed, dropped

---

## DERIVE_ONBOARDING_GUIDANCE

```kotlin
val actionType: String                        // required, "DERIVE_ONBOARDING_GUIDANCE"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always false
val workingCalorieTarget: Int                 // required, must be > 0
val calorieAdjustment: Int                    // required, working target minus deterministic baseline
val suggestedProteinRange: String             // required
val suggestedCarbRange: String                // required
val suggestedFatRange: String                 // required
val derivationExplanation: String             // required, conversational explanation for the user
val notes: String?                            // nullable, implementation notes or caveats
```

Contract notes:
- `workingCalorieTarget` maps to `NutritionGuidance.calorieTarget`.
- `UserProfile.calorieTarget` remains the deterministic baseline and must never be overwritten by this action.
- When no AI adjustment is needed, `workingCalorieTarget` should equal the deterministic baseline and `calorieAdjustment` should be `0`.

Silent correction rules:
- `confidence` out of range → clamped to `0.0–1.0`
- `notes` missing → defaulted to `null`
- `workingCalorieTarget <= 0` → treated as invalid and dropped as malformed for onboarding guidance flows

---

## LOG_MEAL

```kotlin
val actionType: String                        // required, "LOG_MEAL"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required
val mealItems: List<MealItemDraft>            // required, must not be empty
val clarificationNeeded: Boolean              // required
val clarificationQuestion: String?            // nullable, null if clarificationNeeded is false
val mealTimeHint: String?                     // nullable, e.g. "breakfast", "lunch", "dinner"
val sourceAssumptions: List<String>           // required, may be empty
```

MealItemDraft:
```kotlin
val name: String                              // required
val quantity: Float?                          // nullable
val unit: String?                             // nullable, e.g. "cup", "slice", "piece"
val portionDescription: String?               // nullable, e.g. "large bowl", "small plate"
val preparationType: String?                  // nullable, e.g. "grilled", "fried", "raw"
val contextHints: List<String>                // required, may be empty
val calories: Int                             // required, must be >= 0
val protein: Float                            // required, must be >= 0
val carbs: Float                              // required, must be >= 0
val fat: Float                                // required, must be >= 0
val confidence: Float                         // required, 0.0–1.0
```

Silent correction rules:
- calories < 0 → set to 0
- protein/carbs/fat < 0 → set to 0
- confidence out of range → clamped to 0.0–1.0

---

## ASK_CLARIFICATION

```kotlin
val actionType: String                        // required, "ASK_CLARIFICATION"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always true
val question: String                          // required
val clarificationType: ClarificationType      // required
val blockingForSave: Boolean                  // required
val roundNumber: Int                          // required, 1–3
```

ClarificationType enum:
- PORTION
- PREPARATION
- SOURCE
- COMPOSITION
- LABEL
- OTHER

Silent correction rules:
- roundNumber < 1 → set to 1
- roundNumber > 3 → treated as final round

---

## SHOW_DAILY_SUMMARY / SHOW_WEEKLY_SUMMARY / SHOW_MONTHLY_SUMMARY

```kotlin
val actionType: String                        // required
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always false
val periodReference: String                   // required, e.g. "2024-04-14" or "week of 2024-04-08"
```

Note: After generating any summary, the corresponding UserContextNarrative must be updated.

---

## SUGGEST_FOOD_IMPROVEMENTS

```kotlin
val actionType: String                        // required, "SUGGEST_FOOD_IMPROVEMENTS"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always false
val focusArea: String                         // required
val suggestions: List<String>                 // required, must not be empty
```

---

## EDIT_EXISTING_MEAL

```kotlin
val actionType: String                        // required, "EDIT_EXISTING_MEAL"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always true
val targetMealEntryId: String                 // required
val proposedChanges: List<String>             // required, must not be empty
```

---

## CARD_DRAFT_MEAL

```kotlin
val actionType: String                        // required, "CARD_DRAFT_MEAL"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always true
val mealItems: List<MealItemDraft>            // required, must not be empty
val surfacedAssumptions: List<String>         // required, all assumptions shown explicitly to user
val clarificationNeeded: Boolean              // required
val clarificationQuestion: String?            // nullable, null if clarificationNeeded is false
val roundNumber: Int                          // required, 1–3
val canSkipClarification: Boolean             // required, true when confidence is sufficient to proceed
```

Silent correction rules:
- same as LOG_MEAL for MealItemDraft fields
- roundNumber clamped to 1–3

---

## CARD_STRESS_TEST_RESULT

```kotlin
val actionType: String                        // required, "CARD_STRESS_TEST_RESULT"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always false
val weakAssumptions: List<String>             // required, may be empty if no issues found
val suggestedFollowUps: List<String>          // required, may be empty
val canProceedAsIs: Boolean                   // required
```

---

## UPDATE_FOOD_REFERENCE

```kotlin
val actionType: String                        // required, "UPDATE_FOOD_REFERENCE"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always true
val foodReferenceId: String                   // required
val updatedCalories: Int?                     // nullable, only present if changed, must be >= 0
val updatedProtein: Float?                    // nullable, only present if changed, must be >= 0
val updatedCarbs: Float?                      // nullable, only present if changed, must be >= 0
val updatedFat: Float?                        // nullable, only present if changed, must be >= 0
val changeReason: String                      // required
```

---

## UNKNOWN

```kotlin
val actionType: String                        // required, "UNKNOWN"
val confidence: Float                         // required, 0.0–1.0
val rationale: String                         // required
val requiresReview: Boolean                   // required, always false
val reason: String                            // required
```

---

## Validation policy

### Layer 1 — Silent correction
Applied to every response before review:
- Negative calorie/macro values → set to 0
- Confidence out of 0.0–1.0 → clamped
- Missing optional nullable fields → defaulted to null

### Layer 2 — Semantic sanity check
After silent correction:
- Implausible quantities (e.g. 500 portions of a single item) → confidence downgraded automatically
- Response proceeds to review with low confidence, not rejected

### Layer 3 — Review card gate
- No value writes to Room without passing through review card and user confirmation
- This gate cannot be bypassed for any reason

### Malformed response handling
- Completely unparseable JSON → dropped, pre-canned failure message shown
- Missing required fields → fallback to UNKNOWN action type
- Wrong contractVersion → treated as malformed, dropped
- No partial state is ever written on failure

## Pre-canned failure messages
- Offline: "You need to be connected to the internet before asking."
- Timeout or unknown: "Something went wrong. Please try again."
- Unrecoverable malformed response: "The AI couldn't process that. Please try again."
