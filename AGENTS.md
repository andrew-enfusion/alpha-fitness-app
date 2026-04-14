# AGENTS.md

## Repository startup rule
Before writing any code, always inspect the repository structure and read the required reference docs fully in the documented order.

## Required session-start read order
1. `README_FIRST.md`
2. `CODEX_MASTER_PROMPT.md`
3. `docs/01_PRODUCT_SPEC.md`
4. `docs/02_ARCHITECTURE_GUIDE.md`
5. `docs/03_DOMAIN_MODEL.md`
6. `docs/04_FEATURE_BUILD_TEMPLATE.md`
7. `docs/05_AI_ACTION_CONTRACT.md`
8. `docs/06_ERROR_HANDLING_POLICY.md`
9. `docs/08_IMPLEMENTATION_ROADMAP.md`
10. `docs/09_NEXT_STEP_PROTOCOL.md`
11. `docs/10_IMPLEMENTATION_STATUS.md`
12. `docs/11_BUG_TRACKING.md`

## Conditional docs
- Read `docs/07_PHOTO_PIPELINE.md` only when the current roadmap phase requires photo logging work.
- Read `docs/12_AI_MEAL_CARD.md` only when the current task touches the AI-assisted meal card flow or Phase 4 planning.

## Default session-start prompt
Read `README_FIRST.md`, `CODEX_MASTER_PROMPT.md`, and all required docs in the documented order.

Follow `docs/02_ARCHITECTURE_GUIDE.md` strictly. Strict-core rules are non-negotiable.

Use `docs/08_IMPLEMENTATION_ROADMAP.md`, `docs/09_NEXT_STEP_PROTOCOL.md`, and `docs/10_IMPLEMENTATION_STATUS.md` to determine the current phase and next smallest valid task.

Implement only the next smallest complete slice. Do not jump ahead in the roadmap. Do not add future-phase features early.

After meaningful progress, update `docs/10_IMPLEMENTATION_STATUS.md`.

If you discover a bug, architecture gap, or document conflict, log it in `docs/11_BUG_TRACKING.md` and stop for clarification before broad expansion.

## Repo grounding prompt
Before writing any code, inspect the repository structure and confirm:
- which docs are present
- which phase is active
- what the current implementation status says
- what the next smallest valid task is

Then summarize the plan before making changes.
