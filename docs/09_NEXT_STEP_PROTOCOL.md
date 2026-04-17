# 09_NEXT_STEP_PROTOCOL.md

## Purpose
Define how Codex selects the next implementation step.

## Required pre-read
Before starting work, read:
- 01_PRODUCT_SPEC.md
- 02_ARCHITECTURE_GUIDE.md
- 03_DOMAIN_MODEL.md
- current phase in 08_IMPLEMENTATION_ROADMAP.md
- 10_IMPLEMENTATION_STATUS.md

## Next-step selection rules
1. Complete prerequisites before feature polish.
2. Prefer deterministic systems before AI convenience features.
3. Prefer text flows before photo flows.
4. Prefer editable and recoverable flows before shortcuts.
5. Do not implement work deferred by roadmap.
6. If docs conflict, stop and resolve docs first.
7. If information architecture or navigation structure is wrong, correct the shell and route foundation before deeper feature logic continues.
8. Onboarding separation, app-entry gating, and top-level route ownership are structural prerequisites, not optional polish.

## Completion rule
A phase item is complete only when:
- architecture boundaries are respected
- major success and error states exist
- status is updated
- known critical bugs are not being ignored

## Documentation update rule
If coding reveals missing behavior rules, update docs before broadening implementation.

## Phase transition rule
- After Phase 2 is complete, Phase 2.5 must be completed before Phase 3 begins.
- Phase 3 must not absorb app-shell redesign, onboarding separation, or top-level navigation correction work.
