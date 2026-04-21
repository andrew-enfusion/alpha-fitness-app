# 15_HANDOFF_PROTOCOL.md

## Purpose
Define when and how `docs/14_LATEST_CONTEXT_REPORT.md` is updated and how it is used to start a new session.

---

## When to regenerate the context report

Regenerate `docs/14_LATEST_CONTEXT_REPORT.md` after any of the following:

- completing any meaningful implementation slice, even a small one
- any architecture doc change that affects how the next task should be approached
- any roadmap or status doc change that changes phase or task ordering
- resolving or opening a bug that affects active implementation work
- any schema migration (Room version bump)
- before ending a session where significant progress was made

Do not let the context report drift from the real codebase. If the report describes something that is not yet implemented, it will mislead the next session.

---

## How to regenerate

1. Open `docs/13_CONTEXT_REPORT_TEMPLATE.md`.
2. Copy the template prompt from that file.
3. Paste it into the current AI session, or a new one with access to the codebase.
4. Replace the contents of `docs/14_LATEST_CONTEXT_REPORT.md` with the generated output.
5. Commit the updated file with the same commit as the implementation work, or as a follow-up commit immediately after.

---

## How to use the context report in a new session

Start the new session with:

```text
Read docs/14_LATEST_CONTEXT_REPORT.md first to restore working context quickly.
Then read README_FIRST.md, CODEX_MASTER_PROMPT.md, and the required source-of-truth docs in the documented order.
Use docs/10_IMPLEMENTATION_STATUS.md, docs/08_IMPLEMENTATION_ROADMAP.md, and docs/09_NEXT_STEP_PROTOCOL.md to confirm the current phase and next task.
Do not start coding until you have summarized the plan.
```

`docs/14_LATEST_CONTEXT_REPORT.md` is the primary continuity artifact for handoff and resume flow, but it does not replace the source-of-truth docs.

---

## What the context report must always contain

- exact phases that are complete, in progress, and deferred
- the last completed slice with specific class and file references
- the next smallest valid task
- all open bugs with severity
- key constraints that must not break
- what is stubbed or placeholder vs actually implemented
- current phase and slice status, including active in-progress work if any

---

## What the context report must never contain

- descriptions of planned or deferred work as if it is implemented
- generic architecture summaries not grounded in real class names
- vague language like "the app handles X" without naming the class that handles it
- information that contradicts the current state of the codebase

---

## Relationship to other docs

- `docs/13_CONTEXT_REPORT_TEMPLATE.md` - stable template, rarely changes
- `docs/14_LATEST_CONTEXT_REPORT.md` - living handoff document, updated frequently
- `docs/10_IMPLEMENTATION_STATUS.md` - authoritative source for phase completion; the context report should reflect it
- `docs/11_BUG_TRACKING.md` - authoritative source for bugs; the context report summarizes open bugs
- `CHANGELOG.md` - authoritative audit trail; the context report does not replace it
