# 08_IMPLEMENTATION_ROADMAP.md

## Purpose
Define implementation phases, dependencies, and deferred work.

## Phase 0 - Documentation foundation
Complete and stabilize docs before significant coding.

## Phase 1 - App skeleton
- Android project structure
- navigation shell
- Room setup
- Hilt setup
- repository interfaces
- base domain scaffolding

## Phase 2 - Onboarding and profile
- AI-guided onboarding flow
- conversational calorie target derivation and explanation
- profile persistence
- editable profile and AI re-derivation on changes
- NutritionGuidance persistence with derivationExplanation

## Phase 2.5 - UI shell, navigation, and layout foundation
- onboarding separated from the main app shell
- app-entry routing based on local persisted onboarding/profile completion state
- main app `Scaffold` with bottom navigation
- top-level destinations: Log, Calendar, Insights, Profile
- Calendar Day Detail route shell nested under Calendar
- removal of any top-level History tab concept
- intentional placeholder layouts for unfinished destinations using the shared design system

## Phase 3 - Text meal logging core
- text AI action handling on the Log destination
- local food lookup inside the Log flow
- estimation engine
- structured review-before-save flow
- save meal flow
- metrics recomputation triggers
- required basic error states for text logging

## Phase 3.5 - Log clarification and low-confidence refinement
- single-cycle low-confidence clarification handling on the Log destination
- explicit `LowConfidence` output state separate from errors and review-ready output
- clarification card with quick options and simple text response
- reinterpretation using original submitted draft plus optional clarification answer
- no chat transcript, no clarification persistence, and no save-flow redesign

## Phase 4 - AI-assisted meal card
- separate meal card entry surface
- seed input and first-pass AI draft
- clarification loop (up to 3 rounds)
- stress test (user-triggered optional pass)
- fast-confirm vs full review card based on confidence
- FoodReference write-back review card
- save through standard flow

## Phase 5 - Meal history detail and editing
- meal history presentation inside Calendar Day Detail
- meal detail
- edit/delete flows
- FoodReference write-back review card on edit
- historical metrics recomputation

## Phase 6 - Calendar data and daily metrics
- daily metrics view (totalCalories vs targetCalories, raw numbers only)
- over-target flagging in UI
- calendar markers
- real day detail data population

## Phase 7 - Insights and context narratives
- daily summaries
- weekly summaries
- monthly summaries
- UserContextNarrative updates after each summary
- charts
- macro guidance UI (guidance framing, not goal framing)

## Phase 8 - Photo logging
- photo input flow
- structured photo action handling
- review and save

## Phase 9 - Optimization and quality
- repeated-meal shortcuts
- confidence threshold tuning based on UI testing
- performance tuning
- prompt refinement
- migration hardening

## Deferral rules
- Do not build photo logging before text logging and meal card are stable.
- Do not build convenience fast paths before edit and review flows are stable.
- Do not introduce sync or multi-user UX in V1.
- Do not hardcode the confidence threshold; keep it a tunable config from day one.
- Do not mix shell and navigation foundation work into Phase 3 text logging once Phase 2.5 has been defined.
