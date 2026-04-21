# Alpha Fitness App — Latest Context Report

> Generated: 2026-04-21 | Based on: commit `0974b2e`
> Regenerate this file using `docs/13_CONTEXT_REPORT_TEMPLATE.md` and `docs/15_HANDOFF_PROTOCOL.md` after each meaningful completed slice.
> This report is the primary continuity artifact for fresh chats and resume flow. It does not replace the source-of-truth docs.

---

## 1. Current feature state

- Complete phases:
  - Phase 0 documentation foundation
  - Phase 1 app skeleton
  - Phase 2 onboarding and profile
  - Phase 2.5 UI shell, navigation, and layout foundation
- Phase 3 text meal logging core is in progress.
- The core Phase 3 Log loop is functionally complete: draft input, interpretation, review-ready state, explicit confirm-save, Room persistence, and daily metrics recompute all work today.
- Remaining Phase 3 work is refinement, not core loop construction. The next work is about clarification and low-confidence fallback behavior inside the existing Log flow.
- Working end-to-end flows today:
  - first-run onboarding and profile persistence
  - deterministic baseline calorie-target derivation
  - separate persisted `NutritionGuidance` working target and explanation
  - onboarding retry and reset-to-baseline guidance behavior
  - app-entry gating into onboarding vs main shell
  - Log text entry -> interpretation -> review -> explicit save -> daily metrics recompute
  - local-first meal-memory matching before gateway fallback
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
  - `LogViewModel` owns draft input, interpretation, review state, save state, and retry behavior
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

- Last completed slice: explicit Log failure-state and retry refinement
  - `LogOutputState` now distinguishes `ValidationError`, `InterpretationTimeout`, `InterpretationMalformed`, and `InterpretationFailure` instead of treating interpretation failure as one generic state
  - `LogUiState.submittedDraft` is now preserved per submission, and `LogViewModel` retries interpretation against that immutable value instead of the live `draftMessage`
  - `LogInterpretationStateCard` now groups timeout, malformed, and generic interpretation failures under one retryable UI pattern
  - `LogSaveState.Failure` now stays separate from interpretation output so save retry can happen from the existing `ReviewReady` state
- Previous slice: local meal-memory matching
  - `InterpretLogMealUseCase` now calls `MealRepository.getRecentSavedMeals(...)` before gateway fallback and runs `LocalMealMemoryMatcher`
  - `LocalMealMemoryMatcher` scores token overlap, phrase containment, and recency over a bounded recent-meal set
  - Local reuse is item-aware through `SavedMealMemory` and avoids replaying unrelated items from an older full meal

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
- `LogViewModel` maps the result to `LogOutputState.ReviewReady`
- User confirms save from the review card
- `ConfirmLogMealSaveUseCase` persists `MealEntry` and `MealItem` through `MealRepository.saveMealAndLoadMealsForDate(...)`
- `DailyMetricsCalculator` recomputes the affected date from scratch
- `DailyMetricsRepository.replaceDailyMetrics(...)` stores the replacement metrics
- `LogViewModel` updates `LogSaveState` and clears back to the post-save UI state

## 6. Interaction rules (Log)

- Save is only allowed from `LogOutputState.ReviewReady`
- Review-before-save remains mandatory for all current Log saves
- Retry interpretation uses preserved `submittedDraft`, not the live input field
- Save retry does not rerun interpretation; it retries from the existing review-ready meal state
- Interpretation failure and save failure remain separate state paths

## 7. Current limitations and gaps

- `LogInterpretationGateway` is still development-only, not a full provider-backed Log gateway
- `FoodReference` write-back and learning are not implemented
- `Calendar`, `Calendar Day Detail`, and `Insights` remain structurally present but functionally placeholder-heavy
- photo logging is not started
- advanced summary and narrative flows are not started
- fast-confirm optimization is not implemented

## 8. Known risks and open bugs

- Open bug:
  - `SEC-001` - High - provider-backed onboarding guidance still lacks a production-safe mobile credential strategy
- Architectural risk:
  - Log interpretation still depends on a development gateway, so the future real gateway slice must preserve current use-case orchestration and review-before-save behavior

## 9. Next logical steps

- Current active phase: Phase 3 text meal logging core
- Exact next smallest valid task:
  - tighten clarification or low-confidence fallback behavior inside Log when neither local meal memory nor the gateway yields a clearly reviewable draft
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
- the context report is a continuity aid, not the authoritative replacement for roadmap, status, bug, or architecture docs

## 11. Current phase / slice status

- Current roadmap phase:
  - Phase 3 text meal logging core
- Most recently completed slice:
  - explicit Log failure-state modeling and cleaner retry behavior
- Active in-progress slice:
  - none at the time this report was generated
- Explicitly deferred work:
  - Phase 4 AI-assisted meal card
  - Phase 5 meal history detail and editing
  - Phase 6 calendar data and daily metrics consumers
  - Phase 7 insights and context narratives
  - Phase 8 photo logging
  - Phase 9 optimization and quality shortcuts
