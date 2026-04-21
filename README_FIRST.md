# Alpha Fitness App - Codex Starter Pack

This repository contains the reference material Codex must read before generating or modifying code.

## Required session-start read order
Read these files at the start of every implementation session, in this order:

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

## Resume-work continuity flow
If work is being resumed from a previous session or a fresh chat:

1. Read `docs/14_LATEST_CONTEXT_REPORT.md` first for the fastest continuity handoff.
2. Then read the required session-start docs in the standard order above.
3. Use `docs/10_IMPLEMENTATION_STATUS.md`, `docs/08_IMPLEMENTATION_ROADMAP.md`, and `docs/09_NEXT_STEP_PROTOCOL.md` to confirm the real source-of-truth phase and next task.

`docs/14_LATEST_CONTEXT_REPORT.md` is the primary handoff artifact for restoring momentum in a new session, but it does not replace the source-of-truth docs.

## Handoff update rule
- Use `docs/13_CONTEXT_REPORT_TEMPLATE.md` to regenerate `docs/14_LATEST_CONTEXT_REPORT.md`.
- Follow `docs/15_HANDOFF_PROTOCOL.md` for when and how the handoff report must be refreshed.

## Conditional docs
- Read `docs/07_PHOTO_PIPELINE.md` only when the current roadmap phase requires photo logging work.
- Read `docs/12_AI_MEAL_CARD.md` when the current task touches the AI-assisted meal card flow or Phase 4 planning.

## Session-start operating rule
Before writing any code:
- inspect the repository structure
- confirm which required docs are present
- identify the active roadmap phase
- read the current implementation status
- determine the next smallest valid task
- summarize the plan before making changes
- if Phase 2.5 exists, finish shell/navigation/layout foundation before Phase 3 text logging work

## Changelog rule
- `CHANGELOG.md` is part of the audit trail for this repo.
- Every meaningful change should add a timestamped entry using local date and time.
- Entries may use AI summaries, but they must stay specific enough to support audit review and targeted reverts.

## Bug tracking rule
- `docs/11_BUG_TRACKING.md` is part of the audit trail for defects and document conflicts.
- Open bugs belong only under `Current open bugs`.
- Resolved bugs belong under `Resolved bugs`.
- Each bug entry should carry tracked and updated timestamps plus a timestamped activity log.

## Product summary
Alpha Fitness App is a single-user, local-first Android app for personal calorie tracking with an AI-assisted nutrition workflow. Room remains the source of truth for structured data.
