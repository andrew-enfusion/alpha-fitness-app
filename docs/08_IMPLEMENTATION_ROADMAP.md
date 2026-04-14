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

## Phase 3 - Text meal logging core
- chat shell
- text AI action handling
- local food lookup
- estimation engine
- review-before-save (full card and fast-confirm path)
- save meal flow
- metrics recomputation triggers

## Phase 4 - AI-assisted meal card
- separate meal card entry surface
- seed input and first-pass AI draft
- clarification loop (up to 3 rounds)
- stress test (user-triggered optional pass)
- fast-confirm vs full review card based on confidence
- FoodReference write-back review card
- save through standard flow

## Phase 5 - History and editing
- meal history
- meal detail
- edit/delete flows
- FoodReference write-back review card on edit
- historical metrics recomputation

## Phase 6 - Calendar and daily metrics
- daily metrics view (totalCalories vs targetCalories, raw numbers only)
- over-target flagging in UI
- calendar markers
- day detail screen

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
- Do not build convenience fast paths before edit/review/history flows are stable.
- Do not introduce sync or multi-user UX in V1.
- Do not hardcode the confidence threshold — keep it a tunable config from day one.
