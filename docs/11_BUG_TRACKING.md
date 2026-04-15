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

- BUG_ID: ARCH-001
- TITLE: Phase 2 guidance persistence rule conflicts with final target storage rule
- TYPE: Architecture violation
- SEVERITY: High
- DATE_TRACKED: 2026-04-14 21:23:03 -04:00
- LAST_UPDATED: 2026-04-14 21:34:29 -04:00
- RESOLVED_AT: 2026-04-14 21:34:29 -04:00
- DESCRIPTION: `docs/02_ARCHITECTURE_GUIDE.md` said "The final target stored is the AI-adjusted value, not the raw formula output," while `docs/10_IMPLEMENTATION_STATUS.md` said the next Phase 2 slice still needed a decision about how AI-adjusted targets are persisted relative to the deterministic baseline. The current implementation also persisted the deterministic baseline on `UserProfile` and stored guidance separately, which matched one possible interpretation but not the strict wording of the architecture guide.
- REPRODUCTION STEPS: Read the `Onboarding rules` section in `docs/02_ARCHITECTURE_GUIDE.md`, then compare it to the `next smallest valid task` note in `docs/10_IMPLEMENTATION_STATUS.md` and the current persisted onboarding implementation.
- EXPECTED BEHAVIOR: The docs should state one consistent rule for whether AI-adjusted targets overwrite `UserProfile.calorieTarget`, live only in `NutritionGuidance`, or require both values with clearly named ownership.
- ACTUAL BEHAVIOR: The docs left the persistence contract ambiguous right before the provider-backed AI onboarding slice.
- ROOT CAUSE: The repo evolved the separate `NutritionGuidance` path before the final storage contract for AI-adjusted targets was fully settled across the architecture and implementation-status docs.
- AFFECTED COMPONENTS: Phase 2 onboarding guidance, `UserProfile`, `NutritionGuidance`, future AI onboarding gateway, auditability of calorie-target history
- FIX STRATEGY: Adopt the explicit dual-target rule: keep `UserProfile.calorieTarget` as the deterministic baseline, keep `NutritionGuidance.calorieTarget` as the working target used by the app, and update the docs before continuing implementation.
- STATUS: Resolved
- ACTIVITY LOG:
  - `2026-04-14 21:23:03 -04:00 | tracked` Identified a cross-document conflict while grounding the next Phase 2 provider-backed onboarding slice.
  - `2026-04-14 21:34:29 -04:00 | resolved` Adopted the explicit dual-target ownership rule, updated the architecture and domain docs, and implemented the onboarding baseline-versus-working-target flow plus reset-to-baseline support.

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
