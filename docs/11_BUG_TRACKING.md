# 11_BUG_TRACKING.md

## Purpose
Define bug logging, classification, prioritization, and architecture feedback rules.

## Bug types
- Data integrity
- AI interpretation
- UI behavior
- Flow/navigation
- Performance
- Offline/network
- Architecture violation

## Severity levels
- Critical
- High
- Medium
- Low

## Status values
- Open
- In Progress
- Blocked
- Resolved
- Closed

## Tracking rules
- Open bugs must appear only under `## Current open bugs`.
- Resolved or closed bugs must not appear under `## Current open bugs`.
- Resolved or closed bugs belong under `## Resolved bugs`.
- Every bug entry must include `DATE_TRACKED` and `LAST_UPDATED`.
- Resolved or closed bugs must also include `RESOLVED_AT`.
- Every bug must include an `ACTIVITY LOG` with timestamped updates in local time.
- Activity log entries should be concise, concrete, and audit-friendly.

## Bug entry format
- BUG_ID:
- TITLE:
- TYPE:
- SEVERITY:
- DATE_TRACKED:
- LAST_UPDATED:
- RESOLVED_AT:
- DESCRIPTION:
- REPRODUCTION STEPS:
- EXPECTED BEHAVIOR:
- ACTUAL BEHAVIOR:
- ROOT CAUSE:
- AFFECTED COMPONENTS:
- FIX STRATEGY:
- STATUS:
- ACTIVITY LOG:

## Fix rules
- Do not patch symptoms only.
- If a bug reveals a missing architecture rule, update the relevant doc.
- Critical bugs block phase progression.
- Check cross-impact before closing a bug.

## Current open bugs

None recorded at this time.

## Resolved bugs

- BUG_ID: DOC-001
- TITLE: Narrative injection rule conflicts between domain model and architecture guide
- TYPE: Architecture violation
- SEVERITY: Medium
- DATE_TRACKED: 2026-04-14 11:01:05 -04:00
- LAST_UPDATED: 2026-04-14 12:07:25 -04:00
- RESOLVED_AT: 2026-04-14 11:04:25 -04:00
- DESCRIPTION: `docs/03_DOMAIN_MODEL.md` implied that `UserContextNarrative` records were injected into every AI call, while `docs/02_ARCHITECTURE_GUIDE.md` required task-specific context injection and explicitly rejected blanket injection.
- REPRODUCTION STEPS: Read the `UserContextNarrative` note in `docs/03_DOMAIN_MODEL.md`, then compare it with the `AI context injection rule` section in `docs/02_ARCHITECTURE_GUIDE.md`.
- EXPECTED BEHAVIOR: Both docs should define the same task-specific context-injection policy.
- ACTUAL BEHAVIOR: The docs gave different rules for when narratives were injected.
- ROOT CAUSE: The starter pack carried an older domain-model note that was not updated when the architecture guide was tightened.
- AFFECTED COMPONENTS: Documentation, AI gateway design, summary and meal interpretation flows
- FIX STRATEGY: Update `docs/03_DOMAIN_MODEL.md` so narratives are available but task-specific only, reinforce the same rule in `docs/02_ARCHITECTURE_GUIDE.md`, and keep the resolved issue in the resolved section rather than the open section.
- STATUS: Resolved
- ACTIVITY LOG:
  - `2026-04-14 11:01:05 -04:00 | tracked` Conflict identified during starter-pack review.
  - `2026-04-14 11:04:25 -04:00 | resolved` Updated the domain and architecture docs to make narrative injection explicitly task-specific.
  - `2026-04-14 12:07:25 -04:00 | audit-cleanup` Moved the resolved issue out of the open section and aligned the bug record with the new tracking format.
