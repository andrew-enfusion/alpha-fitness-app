# 10_IMPLEMENTATION_STATUS.md

## Purpose
Track what is complete, in progress, blocked, and deferred.

## Current status
- Phase 0 documentation: starter pack installed in the repository and complete enough to begin implementation; continue evolving docs if gaps are found.
- Phase 1 app skeleton: complete. The project structure, navigation shell, Room shell, Hilt skeleton, repository interfaces, shared wrappers, and theme shell are scaffolded and now verified through Gradle configuration, `:app:assembleDebug`, and `:app:testDebugUnitTest`.
- Phase 2 onboarding and profile: in progress. The deterministic baseline slices are complete, and the first narrow guidance slice is now in place: local `UserProfile` persistence flows through Room, repository, and use-case layers; the onboarding state and screen shell are in place; the app derives and persists a baseline calorie target locally using deterministic profile inputs; and onboarding now persists a separate `NutritionGuidance` record with saved explanation and macro framing while keeping the baseline target unchanged.
- Phase 3 text meal logging core: not started.
- Phase 4 AI-assisted meal card: not started.
- Phase 5 history and editing: not started.
- Phase 6 calendar and daily metrics: not started.
- Phase 7 insights and context narratives: not started.
- Phase 8 photo logging: deferred until text logging and meal card are stable.
- Phase 9 optimization and quality: deferred.

## Notes
- Required repo startup docs and Codex session guidance are now present in the repository root.
- Current active implementation phase is still Phase 2 onboarding and profile.
- The deterministic onboarding/profile persistence, baseline calorie-target derivation, and first persisted guidance slice are implemented and verified locally with `:app:assembleDebug` and `:app:testDebugUnitTest` using the installed Android Studio JBR with daemon-safe verification settings.
- `NutritionGuidance` is now stored separately from `UserProfile`, and the current development guidance gateway intentionally keeps the deterministic baseline target unchanged while adding explanation and macro guidance metadata.
- The provider-backed AI onboarding slice is currently blocked by `ARCH-001` because the docs do not yet agree on whether AI-adjusted targets overwrite `UserProfile.calorieTarget`, live only in `NutritionGuidance`, or require both values with explicit ownership.
- The next smallest valid task after resolving `ARCH-001` is the provider-backed AI onboarding slice: replace the local development guidance gateway with a real structured AI guidance path using the chosen storage contract and preserve the same auditability boundaries.
- The previous narrative-context documentation conflict has been resolved and reflected in the architecture and domain docs.
- Bug tracking now distinguishes open and resolved issues explicitly and requires timestamped activity for auditability.

## Rules
- Update this file after completing meaningful tasks.
- Record blocked items and dependencies clearly.
- Do not mark a phase complete while critical bugs remain unresolved in that phase.
