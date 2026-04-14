# 02_ARCHITECTURE_GUIDE.md

## Purpose
Define the strict-core architecture rules and the flexible-edge guidance.

## Strict core

### Source of truth
- Room is the canonical source of truth.
- Chat history is not nutrition truth.
- AI output is not authoritative until validated, corrected, and normalized.

### Layering
- Presentation: Compose UI, ViewModels, UI state only.
- Domain: use cases, business rules, deterministic logic.
- Data: Room, repositories, AI gateway, caching.
- Core: shared utilities, time, result wrappers.

### Data flow
`UI -> ViewModel -> UseCase -> Repository -> Room`
Optional AI usage only through repository-owned gateway paths.

### AI boundaries
- AI may interpret input, ask clarifications, and generate summaries.
- AI must not write directly to storage.
- AI must return structured responses conforming to the action contract.
- AI results must pass through validation, silent correction, and sanity checks before any review card is shown.
- Review card is always the final gate before persistence. No value ever writes to Room without user confirmation.

### Onboarding rules
- AI guides the user conversationally through age, sex, height, weight, exercise level, job activity, and primary goal.
- Calorie target is computed deterministically by the app using Mifflin-St Jeor BMR + activity multiplier as the baseline.
- The deterministic baseline currently supports male and female profile sex values only.
- AI may adjust the baseline target up or down based on contextual factors not captured by the formula (e.g. highly physical job on top of heavy exercise, injury/recovery).
- Any AI adjustment is explained conversationally so the user understands why their target differs from the raw formula output.
- The final target stored is the AI-adjusted value, not the raw formula output.
- Macros are not explicit user goals in V1.
- When profile is updated, the app recomputes the baseline and AI re-derives and re-explains the new target.
- Historical meal records are never rewritten when profile changes.

### Estimation rules
- Estimates are approximate by design.
- Restaurant meals use pessimistic assumptions.
- Home-cooked meals use more optimistic assumptions.
- Clarifications are asked only when they materially change the estimate.
- Users are never asked for grams, exact weights, or precise measurements.

### Feature rules
- Every major feature must use ViewModel, UseCase, Repository boundaries.
- No business logic in Compose UI.
- No provider-specific logic in UI or feature packages.
- Metrics and insights read from structured models, not raw chat.

## Flexible edge
Allowed variation:
- exact package naming
- helper placement
- mapper organization
- chart implementation details
- module splitting timing

## Metrics derivation rule
Whenever a meal is created, updated, or deleted, the affected day's metrics must be recomputed immediately and persisted.
If a past meal is edited, that historical day must be recomputed.
Calorie progress is always expressed as raw numbers (totalCalories vs targetCalories).
Going over target is flagged in the UI according to the user's current goal type.
No scores or percentages anywhere.

## Profile update rule
User profile is editable after onboarding. Changes trigger app-side recomputation of the baseline calorie target followed by AI re-derivation and conversational explanation. Historical meal records are not rewritten.

## Offline rule
When offline:
- local history, calendar, metrics, and saved insights remain viewable
- manual meal entry remains available
- AI interpretation and AI summaries are unavailable
- the UI shows a pre-canned message: "You need to be connected to the internet to use AI features"

## Pre-canned AI failure messages
All AI failure states surface a clean human-readable message. No error codes shown to users.
- Offline / no network: "You need to be connected to the internet before asking."
- Timeout or unknown failure: "Something went wrong. Please try again."
- Malformed response (unrecoverable): "The AI couldn't process that. Please try again."
These messages are defined centrally and reused across all failure surfaces.

## AI response validation rule
All AI responses pass through a three-layer validation process before any review card is shown:

Layer 1 — Silent correction:
- Negative calorie or macro values are set to zero.
- Confidence values outside 0.0–1.0 are clamped.
- Missing optional fields are defaulted.
- No user-visible indication of silent corrections.

Layer 2 — Semantic sanity check:
- Implausible quantities (e.g. 500 chicken breasts) are flagged as low confidence automatically.
- Responses that pass layer 1 but fail sanity are not rejected — they proceed to review with confidence downgraded.

Layer 3 — Review card gate:
- No corrected or sanity-flagged value ever writes to Room without going through the review card.
- User always has final confirmation authority.
- This gate is permanent and cannot be bypassed by any layer above it.

Completely malformed or unparseable responses are dropped entirely. A pre-canned failure message is shown. No partial state is written.

## Confidence behavior rule
Confidence scores must influence behavior:
- High-confidence, previously-seen meals show a lightweight fast-confirm path (one tap to save).
- Low-confidence or new meals show the full review card.
- The confidence threshold is a tunable config value, never hardcoded.
- Low-confidence entries must be clearly labeled and must not bypass review.

## AI context injection rule
Context injection is task-specific. Each task type defines exactly what gets injected. No blanket "inject everything" rule.

### AI Context Injection Rule

AI context must be assembled using task-specific rules.

No data source - including narratives, chat history, metrics, or recent meals - may be automatically included in every AI request by default.

All context included in an AI request must be explicitly justified by the current task.

Meal interpretation (chat or card):
- User profile
- Today's meals so far
- Recent chat window (last ~20 messages)
- Daily narrative only

Daily summary generation:
- User profile
- Today's full meal records
- Daily narrative
- Recent chat window

Weekly/monthly summary generation:
- User profile
- Relevant DailyMetrics for the period
- Weekly and monthly narratives
- No chat history

Food reference lookup or update:
- The specific FoodReference record
- The meal item being compared
- Recent usage of that food
- No narratives

Onboarding and profile changes:
- User profile only
- No meal history, no narratives

Stress test (meal card):
- Current meal draft and its assumptions
- User profile
- Daily narrative
- No chat history

## Food Memory write-back rule
- Editing a meal item does not automatically update FoodReference.
- After a user edits a meal item, the app checks whether the edited values differ from the stored FoodReference.
- If they differ, a review card is shown asking the user if they want to update their default for that food.
- If the user confirms, FoodReference is updated.
- If the user dismisses, the edit applies to that meal only and FoodReference is unchanged.
- FoodReference can also be updated when the user explicitly requests it via chat.

## User context narrative rule
Three separate persistent narrative files are maintained: daily, weekly, and monthly.
- Each is updated when its corresponding summary assessment is generated.
- Narratives capture eating behaviors in detail — home cooking habits, restaurant patterns, typical meal times, foods frequently returned to, how often the user hits their target, weekend vs weekday patterns.
- Free-form narrative format.
- User can view all three narratives but cannot edit them.
- Context injection per task type determines which narratives are included in each AI call.

## Migration rule
Schema changes must include migrations. Destructive migration is forbidden outside development-only builds.

## Anti-patterns
- storing nutrition data only in chat
- AI-only estimation without normalization
- direct DB or AI access from UI
- macro goals added to onboarding
- skipping local lookup before remote inference
- implementing roadmap-deferred features early
- asking user for exact weights, grams, or precise measurements
- auto-updating FoodReference without user confirmation
- injecting all context into every AI call regardless of task
- bypassing the review card gate for any reason
- showing raw error codes or technical errors to users
