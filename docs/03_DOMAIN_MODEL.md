# 03_DOMAIN_MODEL.md

## Purpose
Define the formal domain entities, relationships, and data ownership rules.

## UserProfile
Fields:
- id: String (constant `local_user` in V1)
- age: Int
- sex: Enum
- heightCm: Float
- weightKg: Float
- exerciseLevel: Enum
- jobActivityLevel: Enum
- goalType: Enum
- calorieTarget: Int
- createdAt: Instant
- updatedAt: Instant

## NutritionGuidance
Fields:
- userId: String
- calorieTarget: Int
- suggestedProteinRange: String
- suggestedCarbRange: String
- suggestedFatRange: String
- derivationExplanation: String (AI-generated explanation of how target was reached)
- notes: String
- generatedAt: Instant

## FoodReference
Fields:
- id: String
- canonicalName: String
- servingDescription: String
- calories: Int
- protein: Float
- carbs: Float
- fat: Float
- fiber: Float?
- sourceType: Enum
- confidence: Float
- restaurantBias: Boolean
- lastUsedAt: Instant?
- userCorrectedCount: Int

## FoodAlias
Fields:
- id: String
- alias: String
- foodReferenceId: String

## MealEntry
Fields:
- id: String
- userId: String
- timestamp: Instant
- rawInput: String?
- sourceType: Enum(TEXT, PHOTO, MANUAL, CARD)
- photoUri: String?
- totalCalories: Int
- totalProtein: Float
- totalCarbs: Float
- totalFat: Float
- confidence: Float
- isUserEdited: Boolean
- reviewRequired: Boolean

## MealItem
Fields:
- id: String
- mealEntryId: String
- foodReferenceId: String?
- displayName: String
- quantity: Float?
- unit: String?
- portionDescription: String?
- calories: Int
- protein: Float
- carbs: Float
- fat: Float
- assumptions: String
- confidence: Float

## DailyMetrics
Fields:
- date: LocalDate
- totalCalories: Int
- totalProtein: Float
- totalCarbs: Float
- totalFat: Float
- targetCalories: Int
- mealCount: Int
- lastRecomputedAt: Instant

Note: No adherenceScore field. Calorie progress is always expressed as raw totalCalories vs targetCalories. Going over target is flagged at the UI level. No scores or percentages.

## PeriodInsight
Fields:
- id: String
- periodType: Enum(DAILY, WEEKLY, MONTHLY)
- startDate: LocalDate
- endDate: LocalDate
- summaryText: String
- recommendations: String
- deficiencies: String
- generatedAt: Instant

## UserContextNarrative
Fields:
- id: String
- periodType: Enum(DAILY, WEEKLY, MONTHLY)
- narrativeText: String (free-form AI-maintained narrative)
- updatedAt: Instant

Note: Three records are maintained, one per period type. Updated when the corresponding summary assessment runs. User can view but not edit.

## Narrative Context

Narratives are structured summaries derived from user data such as meals, metrics, and observed patterns.

Narratives are available for AI context injection, but they are not automatically included in every AI call.

The decision to include narratives, and which narratives to include, is defined by `docs/02_ARCHITECTURE_GUIDE.md` and must follow task-specific context injection rules.

Narratives should be included only when they are relevant to the current task, in order to avoid:
- context bloat
- degraded model performance
- unnecessary token usage
- inconsistent outputs

## ChatMessage
Fields:
- id: String
- role: Enum(USER, ASSISTANT, SYSTEM)
- message: String
- timestamp: Instant
- linkedMealEntryId: String? (references the MealEntry ID produced by this message, if any)

## Relationships
- UserProfile -> NutritionGuidance (1:1 current guidance)
- MealEntry -> MealItem (1:N)
- MealItem -> FoodReference (0:1)
- FoodReference -> FoodAlias (1:N)
- DailyMetrics derived from MealEntry records per day
- PeriodInsight derived from DailyMetrics and structured history
- ChatMessage -> MealEntry (0:1 via linkedMealEntryId)
- UserContextNarrative (3 records, one per period type, AI-maintained)

## Data ownership
- Meals: Room
- Food references and aliases: Room
- Metrics: derived then persisted in Room
- Insights: AI-generated from structured facts then persisted in Room
- User context narratives: AI-maintained, persisted in Room, read-only to user
- Chat: stored separately and non-authoritative

## Write-back rule
User edits always update the saved meal. A review card is shown after editing a meal item only when the edited values differ from the stored FoodReference. User must explicitly confirm before FoodReference is updated.
