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

## Bug entry format
- BUG_ID:
- TITLE:
- TYPE:
- SEVERITY:
- DESCRIPTION:
- REPRODUCTION STEPS:
- EXPECTED BEHAVIOR:
- ACTUAL BEHAVIOR:
- ROOT CAUSE:
- AFFECTED COMPONENTS:
- FIX STRATEGY:
- STATUS:

## Fix rules
- Do not patch symptoms only.
- If a bug reveals a missing architecture rule, update the relevant doc.
- Critical bugs block phase progression.
- Check cross-impact before closing a bug.

## Current open bugs

- BUG_ID: DOC-001
- TITLE: Narrative injection rule conflicts between domain model and architecture guide
- TYPE: Architecture violation
- SEVERITY: Medium
- DESCRIPTION: `docs/03_DOMAIN_MODEL.md` states that the three `UserContextNarrative` records are injected into every AI call, while `docs/02_ARCHITECTURE_GUIDE.md` defines task-specific context injection and explicitly forbids blanket injection.
- REPRODUCTION STEPS: Read the `UserContextNarrative` note in `docs/03_DOMAIN_MODEL.md`, then compare it with the "AI context injection rule" section in `docs/02_ARCHITECTURE_GUIDE.md`.
- EXPECTED BEHAVIOR: Both docs should define the same context-injection policy.
- ACTUAL BEHAVIOR: The docs give different rules for when narratives are injected.
- ROOT CAUSE: The starter pack appears to carry an older domain-model note that was not updated when the architecture guide was tightened.
- AFFECTED COMPONENTS: Documentation, AI gateway design, summary and meal interpretation flows
- FIX STRATEGY: Update `docs/03_DOMAIN_MODEL.md` so narratives are available but task-specific only, and reinforce the same rule in `docs/02_ARCHITECTURE_GUIDE.md`.
- STATUS: Resolved
