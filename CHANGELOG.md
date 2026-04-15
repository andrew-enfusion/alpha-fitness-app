# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Audit policy

- Each meaningful change must add or update a changelog entry in the same working session.
- Each entry must include a local timestamp in `YYYY-MM-DD HH:MM:SS +/-HH:MM` format.
- Each entry should include a short scope label so related changes can be traced quickly.
- AI-generated summaries are allowed, but they must stay concrete and audit-friendly.
- Git history remains the authoritative version chain for reverts. When a commit already exists, include the short commit SHA in the changelog entry when practical.

## [Unreleased]

### Added
- `2026-04-14 11:01:05 -04:00 | repo-docs | commit 2a11dea` Installed the Codex starter-pack documentation in the repository root and `docs/`.
- `2026-04-14 11:01:05 -04:00 | agent-guidance | commit 2a11dea` Added `AGENTS.md` with the required session-start read order and repo-grounding workflow.
- `2026-04-14 11:01:05 -04:00 | repo-readme | commit 2a11dea` Added a repository `README.md` describing the current documentation-first setup.
- `2026-04-14 11:17:05 -04:00 | phase1-scaffold` Added the Android Phase 1 scaffold with Gradle build files, official wrapper artifacts, package layering, navigation shell, Hilt skeleton, Room shell, repository interfaces, shared wrappers, theme shell, and a basic unit test.
- `2026-04-14 15:50:26 -04:00 | phase2-onboarding` Added the first deterministic Phase 2 onboarding/profile slice with Room-backed `UserProfile` persistence, onboarding state and save use case wiring, a navigation-backed onboarding screen shell, reusable onboarding UI components, and a baseline unit test for profile-save behavior.
- `2026-04-14 17:56:24 -04:00 | phase2-calorie-target` Added a deterministic calorie-target derivation engine and use case, threaded the derived baseline target into profile persistence, exposed the stored target in onboarding, added unit coverage for derivation and save behavior, and verified the slice with daemon-safe Android Studio JBR Gradle commands.
- `2026-04-14 21:07:35 -04:00 | phase2-guidance` Added the first persisted onboarding-guidance slice with a separate `NutritionGuidance` Room table, repository/use-case wiring, a development onboarding guidance gateway, macro guidance formatting, onboarding guidance UI, and new unit coverage for guidance refresh behavior.

### Changed
- `2026-04-14 12:17:28 -04:00 | phase1-validation` Validated the Android scaffold against the installed Android Studio toolchain, fixed AGP 9 built-in Kotlin compatibility issues, and confirmed `:app:assembleDebug` plus `:app:testDebugUnitTest` pass.
- `2026-04-14 12:07:25 -04:00 | bug-audit` Reworked bug tracking so open and resolved issues are separated clearly and each bug record carries timestamped audit activity.
- `2026-04-14 11:05:09 -04:00 | audit-trail` Added timestamped changelog formatting rules and repo guidance so future changes remain easier to audit and revert.
- `2026-04-14 11:01:05 -04:00 | startup-docs | commit 2a11dea` Updated `README_FIRST.md` and `CODEX_MASTER_PROMPT.md` to match the required read order, conditional doc loading rules, and Alpha Fitness App naming.
- `2026-04-14 11:04:25 -04:00 | narrative-context | commit ed1dc80` Resolved the narrative-context rule conflict between the domain model and architecture guide by making narrative injection explicitly task-specific.
- `2026-04-14 11:17:05 -04:00 | status-tracking` Updated the repository and implementation status docs to reflect that the Phase 1 skeleton exists while local build verification remains blocked on missing Java and Android SDK tooling.
- `2026-04-14 15:50:26 -04:00 | onboarding-design` Updated the shared design-system shell to a blue-and-white palette with rounded shapes and spacing tokens, removed generated Gradle daemon noise from version control, and verified the Phase 2 slice with daemon-safe Gradle commands after Kotlin daemon cache instability.
- `2026-04-14 17:56:24 -04:00 | onboarding-rules` Clarified the deterministic onboarding baseline rule to support male and female profile sex values only in the current formula-driven slice and updated onboarding copy to describe the local app-derived target instead of deferred derivation.
- `2026-04-14 21:07:35 -04:00 | guidance-audit` Updated the architecture and implementation-status docs to record that onboarding guidance is now stored separately from `UserProfile`, while the current development gateway preserves the deterministic baseline target until provider-backed AI adjustment is implemented.
