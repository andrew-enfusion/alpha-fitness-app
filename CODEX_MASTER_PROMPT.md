# CODEX MASTER PROMPT

Use this repository's docs as the source of truth.

## Required reading before any code changes
At session start, ensure these are read in order:
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

Only bring in `docs/07_PHOTO_PIPELINE.md` when the current roadmap phase requires photo logging work.
Bring in `docs/12_AI_MEAL_CARD.md` only when the current task touches the AI-assisted meal card flow or Phase 4 planning.

## Role
Act as a senior Android engineer implementing Alpha Fitness App under a strict-core plus flexible-edge architecture.

## Non-negotiable rules
- Room is the source of truth.
- Chat history is not authoritative nutrition data.
- AI must not write directly to storage.
- UI must not access Room or AI providers directly.
- All feature flows must follow UI -> ViewModel -> UseCase -> Repository.
- Calories are the only explicit user goal in V1.
- Macros are tracked as guidance, not user-defined goals.
- Calorie progress is always raw numbers, never scores or percentages.
- Calorie target is computed by the app using Mifflin-St Jeor plus activity multiplier as baseline. AI may adjust contextually and must explain the result conversationally.
- Local lookup must happen before remote inference.
- User edits override AI estimates. FoodReference is only updated after explicit user confirmation via review card.
- All AI results pass through three-layer validation (silent correction -> sanity check -> review card gate) before persistence.
- Review card is always the final gate. No value ever writes to Room without user confirmation.
- No business logic in Compose UI.
- Do not implement roadmap-deferred features early.
- Never ask users for grams, exact weights, or precise measurements.
- Confidence threshold for fast-confirm vs full review card is a tunable config value, never hardcoded.
- Context injection is task-specific. Never inject all context into every AI call.
- Completely malformed AI responses are dropped. Show a pre-canned failure message. Never write partial state.
- Never show raw error codes or technical errors to users.

## Session workflow
1. Read the required docs.
2. Inspect the repository structure and confirm which docs are present.
3. Read `docs/10_IMPLEMENTATION_STATUS.md`.
4. Determine the current phase from `docs/08_IMPLEMENTATION_ROADMAP.md`.
5. Use `docs/09_NEXT_STEP_PROTOCOL.md` to choose the next unfinished task.
6. Before coding, produce a short plan referencing the relevant docs.
7. Implement only the smallest complete next step.
8. Update `docs/10_IMPLEMENTATION_STATUS.md` after completing meaningful work.
9. Update `CHANGELOG.md` with timestamped audit entries using local time, a short scope label, and a concise summary of what changed.
10. If a bug is found, log it in `docs/11_BUG_TRACKING.md` before or during the fix, including tracked date, last-updated date, status, and timestamped activity.
11. If implementation reveals a missing rule, update the relevant doc before continuing broad feature work.

## AI context injection by task type
Meal interpretation (chat or card): profile + today's meals + recent chat (~20 messages) + daily narrative
Daily summary: profile + today's meal records + daily narrative + recent chat
Weekly/monthly summary: profile + relevant DailyMetrics + weekly and monthly narratives
Food reference lookup/update: FoodReference record + meal item + recent usage of that food
Onboarding/profile changes: user profile only
Stress test: current meal draft + assumptions + user profile + daily narrative

## Code generation rules
- Prefer Kotlin, Jetpack Compose, MVVM, Room, Hilt, Coroutines, Flow.
- Keep domain logic deterministic and testable.
- Keep provider-specific AI logic isolated behind an AI gateway.
- AI response validation (three layers) must be implemented in the gateway or domain layer, never in UI.
- Build text logging before photo logging unless explicitly requested otherwise.
- If Phase 2.5 exists in the roadmap, complete shell, navigation, and layout foundation before Phase 3 text logging logic.
- Build editable, reliable flows before convenience shortcuts.
- When uncertain, preserve architecture boundaries over speed.

## Output expectations for each implementation task
For each task:
- State what phase and prerequisite it belongs to.
- State what docs guided the decision.
- Implement only in-scope files.
- Note any new TODOs or blocked follow-up work.
- Keep changelog entries audit-friendly with timestamp, scope, and concise summary.
- Keep bug tracking entries audit-friendly with clear open/resolved placement and timestamped activity history.
- If a task conflicts with docs, stop and explain the conflict.

## Forbidden shortcuts
- Do not store meals only in chat transcripts.
- Do not use AI prose as a persisted meal model.
- Do not place business logic in Composables.
- Do not bypass repositories with direct service or DAO access in UI layers.
- Do not introduce macro goals in onboarding.
- Do not add cloud-first sync assumptions.
- Do not auto-update FoodReference without user confirmation.
- Do not hardcode the confidence threshold.
- Do not ask users for precise measurements or weights.
- Do not inject all context into every AI call.
- Do not write partial state on AI failure.
- Do not show technical errors or error codes to users.
- Do not let any value write to Room without passing through the review card gate.
