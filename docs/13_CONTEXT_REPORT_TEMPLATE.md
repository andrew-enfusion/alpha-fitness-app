# 13_CONTEXT_REPORT_TEMPLATE.md

## Purpose
Store the exact template and instructions for generating the context handoff report used to restore project state in a new Codex or AI session.

## When to use this template
Use this template to regenerate `docs/14_LATEST_CONTEXT_REPORT.md` after:
- completing any meaningful implementation slice
- any architecture change or doc update that affects how Codex should behave
- any roadmap or status change that affects the next implementation step
- before starting a new Codex session on a complex or multi-day task

## Generation instructions
Paste the following prompt into the AI session to generate a new context report. The output should fully replace `docs/14_LATEST_CONTEXT_REPORT.md`.

---

## Template prompt

You are generating a CONTEXT REPORT for the Alpha Fitness App Android project after making changes.

Your output will be copied into a new Codex or AI session to restore working context quickly. Be precise, structured, and concise. Do not explain basic concepts. Reference actual class names, file paths, and layer boundaries from this codebase.

GOAL:
Summarize the current state of the codebase - what is implemented, what changed, what remains incomplete, and what the next task is.

INSTRUCTIONS:

1. CURRENT FEATURE STATE
- What phases are complete?
- What is now functional end-to-end?
- What user flows work today?

2. ARCHITECTURE AS IT EXISTS NOW
- Layer structure (Presentation / Domain / Data / Core) and key classes in each
- ViewModel structure - list key ViewModels and their responsibilities
- State management - how UiState, UiEvent, and sealed interfaces are used
- Data flow - from input to Room, including use case and repository chain
- AI gateway structure - how the gateway is wired, what contracts exist
- Room schema - tables, version, key relationships

3. WHAT CHANGED RECENTLY (DELTA)
- What was the last completed slice?
- What was added or refactored?
- Why was the change made (1-2 lines max per item)?

4. KEY MODELS AND STRUCTURES
- Core domain models (UserProfile, NutritionGuidance, MealEntry, MealItem, DailyMetrics)
- Key sealed interfaces (AppResult, AppError, LogOutputState, LogSaveState, etc.)
- AI action contract summary (what action types exist, what the envelope looks like)

5. CURRENT LIMITATIONS AND GAPS
- What is stubbed, placeholder, or not yet wired?
- What flows are scaffolded but not implemented?
- What is intentionally deferred?

6. KNOWN RISKS AND OPEN BUGS
- List all open bugs from `docs/11_BUG_TRACKING.md` with their severity and current status
- Note any architectural risks or tech debt that could affect future slices

7. NEXT LOGICAL STEPS
- What is the exact next smallest valid task?
- What must not be touched yet (deferred work)?

8. CONSTRAINTS TO PRESERVE
- Non-negotiable rules that must not break
- Specific behaviors that must remain intact across future slices

9. CURRENT PHASE / SLICE STATUS
- Current roadmap phase
- Most recently completed slice
- Active in-progress slice, if any
- Explicitly deferred work

FORMAT:
- Use clear section headers
- Use bullet points, not paragraphs
- Reference real class names (for example `LogViewModel`, `InterpretLogMealUseCase`, `RoomMealRepository`)
- Do not include code unless a snippet is critical to understanding a constraint
- Keep total length moderate but information-dense
- Write assuming the reader has never seen this project before - this report must re-establish continuity quickly

IMPORTANT:
Only describe what is actually implemented in the codebase. Do not describe planned or deferred work as if it exists. Distinguish clearly between implemented, stubbed, and not started.
