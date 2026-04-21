# Alpha Fitness App — Latest Context Report

> Generated: 2026-04-21 | Based on: working tree after the Phase 3.5 Log clarification refinement slice
> Regenerate this file using `docs/13_CONTEXT_REPORT_TEMPLATE.md` and `docs/15_HANDOFF_PROTOCOL.md` after each meaningful completed slice.
> This report is the primary continuity artifact for fresh chats and resume flow. It does not replace the source-of-truth docs.

---

## 1. Current feature state

- Complete phases:
  - Phase 0 documentation foundation
  - Phase 1 app skeleton
  - Phase 2 onboarding and profile
  - Phase 2.5 UI shell, navigation, and layout foundation
  - Phase 3.5 Log clarification and low-confidence refinement
- Phase 3 text meal logging core is still in progress.
- The core Phase 3 Log loop is functionally complete: draft input, interpretation, review-ready state, explicit confirm-save, Room persistence, and daily metrics recompute all work today.
- Remaining Phase 3 work is no longer about core loop construction. The main remaining text-logging milestone is replacing the development fallback with a real structured provider-backed `LOG_MEAL` interpretation path.
- Working end-to-end flows today:
  - first-run onboarding and profile persistence
  - deterministic baseline calorie-target derivation
  - separate persisted `NutritionGuidance` working target and explanation
  - onboarding retry and reset-to-baseline guidance behavior
  - app-entry gating into onboarding vs main shell
  - Log text entry -> interpretation -> review -> explicit save -> daily metrics recompute
  - local-first meal-memory matching before gateway fallback
  - single-cycle low-confidence clarification before reinterpretation when a draft is too uncertain
  - explicit Log failure and retry behavior with preserved submitted draft

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
- Main Log flow:
  - `LogViewModel`
  - `PrepareLogComposerSubmissionUseCase`
  - `InterpretLogMealUseCase`
  - `MealRepository`
  - `DevelopmentLogInterpretationGateway` fallback path
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

- Last completed slice: single-cycle low-confidence clarification refinement
  - `LogOutputState` now includes `LowConfidence`, separate from both interpretation errors and `ReviewReady`
  - `InterpretLogMealUseCase` now accepts `originalSubmittedDraft` plus optional `clarificationAnswer`, and can return either `ReviewReady` or `LowConfidence` on success
  - `LogViewModel` reruns interpretation using the preserved original draft plus one clarification answer without overwriting the original meal description
  - `LogClarificationCard` adds a focused clarification surface with quick options, optional short text input, and no chat transcript UI
- Previous slice: explicit Log failure-state and retry refinement
  - `LogUiState.submittedDraft` is preserved per submission and powers interpretation retry
  - `LogInterpretationStateCard` groups timeout, malformed, and generic interpretation failures under one retryable UI pattern
  - `LogSaveState.Failure` stays separate from interpretation output so save retry happens from the existing `ReviewReady` state

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
- Key sealed/state structures:
  - `AppResult`
  - `AppError`
  - `LogOutputState`
  - `LogSaveState`
  - `ConfirmLogMealSaveOutcome`
- AI contract status:
  - onboarding guidance contract is defined and wired
  - broader AI action contract is documented
  - Log still uses a development interpretation gateway rather than a full provider-backed `LOG_MEAL` path

## 5. Data flow snapshot

- User enters text in the Log composer
- `LogViewModel` handles `SubmitClicked`
- `PrepareLogComposerSubmissionUseCase` validates and trims the draft
- `InterpretLogMealUseCase` orchestrates interpretation
- `MealRepository.getRecentSavedMeals(...)` provides bounded recent confirmed meals
- `LocalMealMemoryMatcher` attempts deterministic local-first matching
- If local confidence is insufficient, `MealRepository.interpretWithGateway(...)` uses `DevelopmentLogInterpretationGateway`
- If the first-pass draft is still too uncertain, `InterpretLogMealUseCase` returns `LogOutputState.LowConfidence`
- User answers one clarification through `LogClarificationCard`
- `LogViewModel` reruns `InterpretLogMealUseCase` with the original submitted draft plus the clarification answer
- `LogViewModel` maps the clarified result to `LogOutputState.ReviewReady`
- User confirms save from the review card
- `ConfirmLogMealSaveUseCase` persists `MealEntry` and `MealItem` through `MealRepository.saveMealAndLoadMealsForDate(...)`
- `DailyMetricsCalculator` recomputes the affected date from scratch
- `DailyMetricsRepository.replaceDailyMetrics(...)` stores the replacement metrics
- `LogViewModel` updates `LogSaveState` and clears back to the post-save UI state

## 6. Interaction rules (Log)

- Save is only allowed from `LogOutputState.ReviewReady`
- Review-before-save remains mandatory for all current Log saves
- Retry interpretation uses preserved `submittedDraft`, not the live input field
- Clarification uses the preserved original submitted draft plus one optional clarification answer
- Only one clarification cycle is allowed in this slice
- Save retry does not rerun interpretation; it retries from the existing review-ready meal state
- Interpretation failure and save failure remain separate state paths

## 7. Current limitations and gaps

- `LogInterpretationGateway` is still development-only, not a full provider-backed structured `LOG_MEAL` gateway
- `FoodReference` write-back and learning are not implemented
- `Calendar`, `Calendar Day Detail`, and `Insights` remain structurally present but functionally placeholder-heavy
- photo logging is not started
- advanced summary and narrative flows are not started
- fast-confirm optimization is not implemented

## 8. Known risks and open bugs

- Open bug:
  - `SEC-001` - High - provider-backed onboarding guidance still lacks a production-safe mobile credential strategy
- Architectural risk:
  - Log interpretation still depends on a development gateway, so the future real provider-backed gateway slice must preserve current low-confidence clarification, local-first orchestration, and review-before-save behavior

## 9. Next logical steps

- Current active phase: Phase 3 text meal logging core
- Exact next smallest valid task:
  - replace the development-only Log interpretation fallback with a real provider-backed structured `LOG_MEAL` interpretation path while preserving local-first matching and the new low-confidence clarification behavior
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
- local-first lookup must happen before gateway fallback
- `ViewModel -> UseCase -> Repository/Gateway` boundaries remain mandatory
- save failure must not collapse interpretation state into a generic error
- retry must use preserved submitted draft, not mutable live input
- clarification must not become a chat transcript or persisted message history
- the context report is a continuity aid, not the authoritative replacement for roadmap, status, bug, or architecture docs

## 11. Current phase / slice status

- Current roadmap phase:
  - Phase 3 text meal logging core, with Phase 3.5 clarification refinement complete
- Most recently completed slice:
  - bounded single-cycle low-confidence clarification handling for Log
- Active in-progress slice:
  - none at the time this report was generated
- Explicitly deferred work:
  - Phase 4 AI-assisted meal card
  - Phase 5 meal history detail and editing
  - Phase 6 calendar data and daily metrics consumers
  - Phase 7 insights and context narratives
  - Phase 8 photo logging
  - Phase 9 optimization and quality shortcuts
