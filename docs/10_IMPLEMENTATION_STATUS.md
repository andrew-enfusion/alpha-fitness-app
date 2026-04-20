# 10_IMPLEMENTATION_STATUS.md

## Purpose
Track what is complete, in progress, blocked, and deferred.

## Current status
- Phase 0 documentation: starter pack installed in the repository and complete enough to begin implementation; continue evolving docs if gaps are found.
- Phase 1 app skeleton: complete. The project structure, navigation shell, Room shell, Hilt skeleton, repository interfaces, shared wrappers, and theme shell are scaffolded and now verified through Gradle configuration, `:app:assembleDebug`, and `:app:testDebugUnitTest`.
- Phase 2 onboarding and profile: complete. The deterministic baseline slices are complete, the dual-target guidance foundation is in place, and the provider-backed guidance path now exists: local `UserProfile` persistence flows through Room, repository, and use-case layers; the onboarding state and screen shell are in place; the app derives and persists a baseline calorie target locally using deterministic profile inputs; onboarding persists a separate `NutritionGuidance` record for the working target and explanation; the local guidance path supports baseline-vs-working-target differentiation plus reset-to-baseline behavior; the onboarding guidance gateway supports a structured OpenAI-backed path with validation and fallback to the development gateway when provider config is absent; and the onboarding UI now exposes explicit retry plus an assistant-style explanation surface without broadening into Phase 3 chat.
- Phase 2.5 UI shell, navigation, and layout foundation: complete. The app now makes a local persisted-state launch decision between onboarding and the post-onboarding shell, onboarding remains a dedicated full-screen flow, the old Home screen has been removed, the main shell uses a `Scaffold` with bottom navigation for Log, Calendar, Insights, and Profile, Calendar owns a nested Day Detail route, and unfinished destinations render as intentional design-system-aligned placeholders instead of a single long scaffold page.
- Phase 3 text meal logging core: in progress. The first Log-only slice is complete: Log now has a dedicated `ViewModel`, explicit UI events, message-input state, a submit action wired through a small use case, and an explicit output-placeholder state for empty, pending submitted text, and validation error cases without introducing transcript-style chat history or interpretation logic yet.
- Phase 4 AI-assisted meal card: not started.
- Phase 5 meal history detail and editing: not started.
- Phase 6 calendar data and daily metrics: not started.
- Phase 7 insights and context narratives: not started.
- Phase 8 photo logging: deferred until text logging and meal card are stable.
- Phase 9 optimization and quality: deferred.

## Notes
- Required repo startup docs and Codex session guidance are now present in the repository root.
- Current active implementation phase is now Phase 3 text meal logging core.
- The deterministic onboarding/profile persistence, baseline calorie-target derivation, and first persisted guidance slice are implemented and verified locally with `:app:assembleDebug` and `:app:testDebugUnitTest` using the installed Android Studio JBR with daemon-safe verification settings.
- `NutritionGuidance` is stored separately from `UserProfile`, and the repository now follows the explicit dual-target rule: `UserProfile.calorieTarget` is the deterministic baseline, while `NutritionGuidance.calorieTarget` is the working target used by the app.
- The onboarding UI now exposes baseline versus working target ownership explicitly and supports resetting the working target back to the deterministic baseline without mutating the profile baseline.
- The onboarding guidance gateway now supports a structured provider-backed OpenAI path using the Responses API plus structured outputs, centralized AI failure mapping, and development fallback when provider config is not present.
- The onboarding UI now includes an explicit retry path for assistant guidance refresh and a more assistant-like explanation surface, which completes the remaining Phase 2 UX work.
- Phase 2.5 now adds the local app-entry gate, the dedicated main shell, top-level routes for Log, Calendar, Insights, and Profile, a nested Calendar Day Detail route, and a Profile surface that displays the completed onboarding data while keeping profile editing in a separate full-screen flow.
- Phase 2.5 was verified with `:app:assembleDebug` and `:app:testDebugUnitTest` using the installed Android Studio JBR.
- The first Phase 3 slice now gives the Log destination a composer foundation with explicit output-placeholder state instead of a generic placeholder page.
- The next smallest valid task is the next Log-only Phase 3 slice: connect the submitted composer text to a repository-owned interpretation path and evolve the staged output area into real structured interpretation and review-ready state without broadening into photo, calendar, or insight work.
- `SEC-001` remains open because a production-safe mobile credential strategy is not defined yet. The current provider-backed path is development-only for local testing, but it is not marked critical and does not block Phase 2 completion under the current repo rules.
- The previous narrative-context documentation conflict has been resolved and reflected in the architecture and domain docs.
- Bug tracking now distinguishes open and resolved issues explicitly and requires timestamped activity for auditability.

## Rules
- Update this file after completing meaningful tasks.
- Record blocked items and dependencies clearly.
- Do not mark a phase complete while critical bugs remain unresolved in that phase.
