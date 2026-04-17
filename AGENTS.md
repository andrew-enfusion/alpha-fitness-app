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

## Changelog and audit rule
- Update `CHANGELOG.md` after every meaningful change.
- Each changelog entry must include a local date and time in `YYYY-MM-DD HH:MM:SS +/-HH:MM` format.
- Each changelog entry should include a short scope label and a concise audit-friendly summary.
- AI summaries are allowed if they remain specific and useful for future reverts or audits.
- The changelog update should ship in the same commit as the change whenever practical.

## Bug tracking audit rule
- Update `docs/11_BUG_TRACKING.md` when bugs are discovered, status changes, or fixes land.
- Open bugs must stay under `Current open bugs`.
- Resolved or closed bugs must move to `Resolved bugs`.
- Every bug entry must include `DATE_TRACKED`, `LAST_UPDATED`, and a timestamped `ACTIVITY LOG`.
- Resolved or closed bug entries must also include `RESOLVED_AT`.

## Default session-start prompt
Read `README_FIRST.md`, `CODEX_MASTER_PROMPT.md`, and all required docs in the documented order.

Follow `docs/02_ARCHITECTURE_GUIDE.md` strictly. Strict-core rules are non-negotiable.

Use `docs/08_IMPLEMENTATION_ROADMAP.md`, `docs/09_NEXT_STEP_PROTOCOL.md`, and `docs/10_IMPLEMENTATION_STATUS.md` to determine the current phase and next smallest valid task.

Implement only the next smallest complete slice. Do not jump ahead in the roadmap. Do not add future-phase features early.

If the roadmap defines Phase 2.5, complete the shell, navigation, and layout foundation before starting Phase 3 text logging logic.

Treat onboarding separation, app-entry gating, and top-level route ownership as documentation-controlled architecture work, not optional UI polish.

After meaningful progress, update `docs/10_IMPLEMENTATION_STATUS.md`.
Also update `CHANGELOG.md` with timestamped audit entries for the work completed.

If you discover a bug, architecture gap, or document conflict, log it in `docs/11_BUG_TRACKING.md` with timestamped activity and stop for clarification before broad expansion.

## Repo grounding prompt
Before writing any code, inspect the repository structure and confirm:
- which docs are present
- which phase is active
- what the current implementation status says
- what the next smallest valid task is

Then summarize the plan before making changes.
