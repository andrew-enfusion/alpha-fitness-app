# Alpha Fitness App — Latest Context Report

> Generated: 2026-04-29 | Based on: working tree after the provider-backed Log `LOG_MEAL` slice
> Regenerate this file using `docs/13_CONTEXT_REPORT_TEMPLATE.md` and `docs/15_HANDOFF_PROTOCOL.md` after each meaningful completed slice.
> This report is the primary continuity artifact for fresh chats and resume flow. It does not replace the source-of-truth docs.

---

## 1. Current feature state

- Complete phases:
  - Phase 0 documentation foundation
  - Phase 1 app skeleton
  - Phase 2 onboarding and profile
  - Phase 2.5 UI shell, navigation, and layout foundation
  - Phase 3 text meal logging core
  - Phase 3.5 Log clarification and low-confidence refinement
- Phase 4 AI-assisted meal card is now the active phase.
- The full Log text loop is working end-to-end:
  - draft input
  - local-first meal-memory matching
  - provider-backed structured `LOG_MEAL` fallback
  - strict validation and normalization
  - low-confidence clarification when needed
  - review-ready state
  - explicit confirm-save
  - Room persistence
  - daily metrics recompute
- Working end-to-end flows today:
  - first-run onboarding and profile persistence
  - deterministic baseline calorie-target derivation
  - separate persisted `NutritionGuidance` working target and explanation
  - onboarding retry and reset-to-baseline guidance behavior
  - app-entry gating into onboarding vs main shell
  - Log text entry -> interpretation -> clarification if needed -> review -> explicit save -> daily metrics recompute

## 2. Architecture as it exists now

- Layer structure:
  - Presentation: Compose screens, `ViewModel`s, `UiState`, `UiEvent`
  - Domain: use cases and deterministic engines
  - Data: Room, repositories, gateways, mappers
  - Core: shared result wrappers, errors, dispatchers, time/config support
- Key `ViewModel`s:
  - `AppEntryViewModel` handles onboarding-vs-shell launch routing
  - `OnboardingViewModel` handles profile save, guidance refresh, and reset behavior
  - `ProfileViewModel` exposes stored baseline and working-target state
  - `LogViewModel` owns draft input, interpretation, clarification, review state, save state, and retry behavior
- Current Log flow boundary:
  - `LogViewModel`
  - `PrepareLogComposerSubmissionUseCase`
  - `InterpretLogMealUseCase`
  - `MealRepository` for local meal-memory lookup only
  - `LogInterpretationGateway` for provider interpretation only
  - `ConfirmLogMealSaveUseCase`
  - `DailyMetricsCalculator`
  - `DailyMetricsRepository`
- Room schema currently persists:
  - `UserProfile`
  - `NutritionGuidance`
  - `MealEntry`
  - `MealItem`
  - `DailyMetrics`

## 3. What changed recently (delta)

- Last completed slice: provider-backed Log `LOG_MEAL` interpretation
  - `InterpretLogMealUseCase` now calls `LogInterpretationGateway` directly after local-first matching instead of routing provider fallback through `MealRepository`
  - `OpenAiLogInterpretationGateway` now calls the OpenAI Responses API with strict JSON-schema structured output for `LOG_MEAL`
  - `LogInterpretationValidator` now enforces contract version and action type, rejects invalid structured output as malformed, and normalizes valid calorie, macro, confidence, and portion fields before review
  - `OpenAiLogInterpretationGateway` keeps `DevelopmentLogInterpretationGateway` only as config-missing fallback while the bound gateway remains the real OpenAI gateway
- Previous slice: bounded low-confidence clarification refinement
  - `LogOutputState` now includes `LowConfidence`
  - `LogViewModel` reruns interpretation using the preserved original draft plus one clarification answer
  - `LogClarificationCard` adds a focused clarification surface with quick options and optional short text input

## 4. Key models and structures

- Core domain models:
  - `UserProfile`
  - `NutritionGuidance`
  - `MealEntry`
  - `MealItem`
  - `DailyMetrics`
  - `LogMealReviewState`
  - `LogMealReviewItem`
  - `SavedMealMemory`
  - `LogClarificationState`
  - `LogClarificationOption`
  - `LogMealInterpretationDraft`
- Key sealed/state structures:
  - `AppResult`
  - `AppError`
  - `LogOutputState`
  - `LogSaveState`
  - `ConfirmLogMealSaveOutcome`
- Provider contract pieces:
  - `LogMealAiContract`
  - `LogMealAiItem`
  - `OpenAiResponsesRequest`
  - `OpenAiResponsesTextConfig`
  - `OpenAiResponsesFormatConfig`

## 5. Data flow snapshot

- User enters text in the Log composer
- `LogViewModel` handles `SubmitClicked`
- `PrepareLogComposerSubmissionUseCase` validates and trims the draft
- `InterpretLogMealUseCase` orchestrates interpretation
- `MealRepository.getRecentSavedMeals(...)` provides bounded recent confirmed meals
- `LocalMealMemoryMatcher` attempts deterministic local-first matching
- If local confidence is insufficient, `LogInterpretationGateway.interpretMealDescription(...)` runs provider interpretation
- `OpenAiLogInterpretationGateway` calls the Responses API with strict `LOG_MEAL` schema output
- `LogInterpretationValidator` validates and normalizes the structured provider result
- `InterpretLogMealUseCase` maps the validated result to:
  - `LogOutputState.ReviewReady`, or
  - `LogOutputState.LowConfidence`, or
  - `AppResult.Failure` which `LogViewModel` maps into explicit interpretation failure states
- If low confidence is returned, `LogClarificationCard` collects one clarification and `LogViewModel` reruns `InterpretLogMealUseCase`
- User confirms save from the review card
- `ConfirmLogMealSaveUseCase` persists `MealEntry` and `MealItem` through `MealRepository.saveMealAndLoadMealsForDate(...)`
- `DailyMetricsCalculator` recomputes the affected date from scratch
- `DailyMetricsRepository.replaceDailyMetrics(...)` stores the replacement metrics
- `LogViewModel` updates `LogSaveState` and clears back to the post-save UI state

## 6. Interaction rules (Log)

- Save is only allowed from `LogOutputState.ReviewReady`
- Review-before-save remains mandatory for all current Log saves
- Local meal-memory matching always runs before provider fallback
- Provider output is never saved directly
- Retry interpretation uses preserved `submittedDraft`, not the live input field
- Clarification uses the preserved original submitted draft plus one optional clarification answer
- Only one clarification cycle is allowed in the current Log clarification slice
- Save retry does not rerun interpretation; it retries from the existing review-ready meal state
- Interpretation failure and save failure remain separate state paths

## 7. Current limitations and gaps

- `DevelopmentLogInterpretationGateway` still exists as config-missing fallback and test support, but the long-term authoritative path is now the OpenAI gateway
- `FoodReference` write-back and learning are not implemented
- `Calendar`, `Calendar Day Detail`, and `Insights` remain structurally present but functionally placeholder-heavy
- photo logging is not started
- advanced summary and narrative flows are not started
- fast-confirm optimization is not implemented

## 8. Known risks and open bugs

- Open bug:
  - `SEC-001` - High - provider-backed onboarding guidance still lacks a production-safe mobile credential strategy
- Architectural risk:
  - the provider-backed Log path now shares the same mobile credential exposure concern as onboarding, even though the app still falls back locally when config is absent

## 9. Next logical steps

- Current active phase: Phase 4 AI-assisted meal card
- Exact next smallest valid task:
  - add a separate AI-assisted meal card entry surface that produces the same review/save-ready path already used by Log
- Must not touch yet:
  - photo logging
  - weekly/monthly summaries
  - fast-confirm optimization
  - calendar or insights expansion
  - FoodReference write-back learning
  - edit/delete history flows

## 10. Constraints to preserve

- `UserProfile.calorieTarget` remains the deterministic baseline and must never be overwritten by AI
- `NutritionGuidance.calorieTarget` is the working target used by the app
- no meal writes to Room without explicit review confirmation
- `DailyMetrics` is recomputed from scratch for the affected date after successful save
- `ViewModel -> UseCase -> Repository + Gateway` boundaries remain mandatory
- repositories remain local data access only
- strict provider schema mismatch is treated as malformed interpretation, not partially recovered
- save failure must not collapse interpretation state into a generic error
- retry must use preserved submitted draft, not mutable live input
- clarification must not become a chat transcript or persisted message history
- the context report is a continuity aid, not the authoritative replacement for roadmap, status, bug, or architecture docs

## 11. Current phase / slice status

- Current roadmap phase:
  - Phase 4 AI-assisted meal card
- Most recently completed slice:
  - provider-backed structured `LOG_MEAL` interpretation for the Log destination
- Active in-progress slice:
  - none at the time this report was generated
- Explicitly deferred work:
  - Phase 5 meal history detail and editing
  - Phase 6 calendar data and daily metrics consumers
  - Phase 7 insights and context narratives
  - Phase 8 photo logging
  - Phase 9 optimization and quality shortcuts
