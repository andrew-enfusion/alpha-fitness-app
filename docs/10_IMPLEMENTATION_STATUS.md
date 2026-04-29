# 10_IMPLEMENTATION_STATUS.md

## Purpose
Track what is complete, in progress, blocked, and deferred.

## Current status
- Phase 0 documentation: starter pack installed in the repository and complete enough to begin implementation; continue evolving docs if gaps are found.
- Phase 1 app skeleton: complete. The project structure, navigation shell, Room shell, Hilt skeleton, repository interfaces, shared wrappers, and theme shell are scaffolded and now verified through Gradle configuration, `:app:assembleDebug`, and `:app:testDebugUnitTest`.
- Phase 2 onboarding and profile: complete. The deterministic baseline slices are complete, the dual-target guidance foundation is in place, and the provider-backed guidance path now exists: local `UserProfile` persistence flows through Room, repository, and use-case layers; the onboarding state and screen shell are in place; the app derives and persists a baseline calorie target locally using deterministic profile inputs; onboarding persists a separate `NutritionGuidance` record for the working target and explanation; the local guidance path supports baseline-vs-working-target differentiation plus reset-to-baseline behavior; the onboarding guidance gateway supports a structured OpenAI-backed path with validation and fallback to the development gateway when provider config is absent; and the onboarding UI now exposes explicit retry plus an assistant-style explanation surface without broadening into Phase 3 chat.
- Phase 2.5 UI shell, navigation, and layout foundation: complete. The app now makes a local persisted-state launch decision between onboarding and the post-onboarding shell, onboarding remains a dedicated full-screen flow, the old Home screen has been removed, the main shell uses a `Scaffold` with bottom navigation for Log, Calendar, Insights, and Profile, Calendar owns a nested Day Detail route, and unfinished destinations render as intentional design-system-aligned placeholders instead of a single long scaffold page.
- Phase 3 text meal logging core: complete. Log now has a dedicated `ViewModel`, explicit UI events, message-input state, a submit action wired through a small use case, a structured interpretation/review-ready state driven through a `ViewModel -> UseCase -> Repository + Gateway` path, an explicit confirm-save flow that persists `MealEntry` and `MealItem` records to Room before recomputing and replacing `DailyMetrics` for the affected date from scratch, a real bounded local-first meal-memory path that matches against recently confirmed saved meals before falling back to the gateway, an explicit Log failure-state model that separates user-fixable validation, retryable interpretation failures, and save failures while preserving the original submitted draft for retry, a bounded single-cycle low-confidence clarification path that keeps clarification separate from chat or persistence concerns, and a real provider-backed structured `LOG_MEAL` fallback path with strict schema validation and normalization before review.
- Phase 3.5 Log clarification and low-confidence refinement: complete. Log can now surface a `LowConfidence` state with a clarification question, quick options, an optional short text clarification, and one reinterpretation cycle using the original submitted draft plus clarification input before falling back to best-effort review or interpretation failure.
- Phase 4 AI-assisted meal card: in progress. The next phase starts from a separate meal-card entry surface and reuses the now-complete Log text interpretation, review, save, and metrics foundations.
- Phase 5 meal history detail and editing: not started.
- Phase 6 calendar data and daily metrics: not started.
- Phase 7 insights and context narratives: not started.
- Phase 8 photo logging: deferred until text logging and meal card are stable.
- Phase 9 optimization and quality: deferred.

## Notes
- Required repo startup docs and Codex session guidance are now present in the repository root.
- Current active implementation phase is now Phase 4 AI-assisted meal card.
- The deterministic onboarding/profile persistence, baseline calorie-target derivation, and first persisted guidance slice are implemented and verified locally with `:app:assembleDebug` and `:app:testDebugUnitTest` using the installed Android Studio JBR with daemon-safe verification settings.
- `NutritionGuidance` is stored separately from `UserProfile`, and the repository now follows the explicit dual-target rule: `UserProfile.calorieTarget` is the deterministic baseline, while `NutritionGuidance.calorieTarget` is the working target used by the app.
- The onboarding UI now exposes baseline versus working target ownership explicitly and supports resetting the working target back to the deterministic baseline without mutating the profile baseline.
- The onboarding guidance gateway now supports a structured provider-backed OpenAI path using the Responses API plus structured outputs, centralized AI failure mapping, and development fallback when provider config is not present.
- The onboarding UI now includes an explicit retry path for assistant guidance refresh and a more assistant-like explanation surface, which completes the remaining Phase 2 UX work.
- Phase 2.5 now adds the local app-entry gate, the dedicated main shell, top-level routes for Log, Calendar, Insights, and Profile, a nested Calendar Day Detail route, and a Profile surface that displays the completed onboarding data while keeping profile editing in a separate full-screen flow.
- Phase 2.5 was verified with `:app:assembleDebug` and `:app:testDebugUnitTest` using the installed Android Studio JBR.
- The first Phase 3 slice now gives the Log destination a composer foundation with explicit output-placeholder state instead of a generic placeholder page.
- The second Phase 3 slice now replaces that placeholder output with real structured review-ready state, keeps orchestration inside `InterpretLogMealUseCase`, keeps repository responsibilities limited to local lookup access and gateway access, and intentionally stubs the local-first lookup path until confirmed meal persistence and write-back exist.
- The third Phase 3 slice now allows save only from the explicit review-ready Log state, persists `MealEntry` and `MealItem` rows to Room, recomputes `DailyMetrics` from scratch for the affected date using all saved meals for that date, and exposes save success/error UI state without broadening into calendar or insight consumers yet.
- The fourth Phase 3 slice now replaces the stubbed local-first interpretation path with deterministic meal-memory matching sourced from a bounded set of recent confirmed saved meals, reuses only matched items rather than blindly replaying whole meals, and still falls back to the gateway when local confidence is too weak.
- The fifth Phase 3 slice now distinguishes user-fixable validation errors from retryable interpretation timeout, malformed, and failed states, keeps save failure separate from interpretation failure, reuses a shared retryable-error UI pattern, and retries interpretation with the original submitted draft rather than the current input field.
- The sixth Log refinement slice now adds a bounded low-confidence clarification path inside Log, introduces a `LowConfidence` output state, allows one clarification cycle without chat persistence, and preserves the existing review-before-save flow.
- The final Phase 3 milestone now replaces the repository-routed development-only Log fallback with a real provider-backed structured `LOG_MEAL` interpretation gateway, keeps local meal-memory matching ahead of the provider call, validates provider output strictly against the contract, normalizes valid outputs before review, and preserves the existing low-confidence clarification and save/error boundaries.
- The next smallest valid task is the first Phase 4 slice: add a separate AI-assisted meal card entry surface that produces the same review/save-ready output path without broadening into photo logging or FoodReference write-back.
- `SEC-001` remains open because a production-safe mobile credential strategy is not defined yet. The current provider-backed path is development-only for local testing, but it is not marked critical and does not block Phase 2 completion under the current repo rules.
- The previous narrative-context documentation conflict has been resolved and reflected in the architecture and domain docs.
- Bug tracking now distinguishes open and resolved issues explicitly and requires timestamped activity for auditability.

## Rules
- Update this file after completing meaningful tasks.
- Record blocked items and dependencies clearly.
- Do not mark a phase complete while critical bugs remain unresolved in that phase.
