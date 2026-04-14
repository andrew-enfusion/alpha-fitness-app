# 12_AI_MEAL_CARD.md

## Purpose
Define the AI-assisted meal card — a separate structured entry path alongside chat for logging meals with conversational AI guidance.

## Overview
The meal card is a dedicated creation surface where the user provides a loose seed description and the AI collaboratively drafts the meal structure through a conversational clarification loop. It is a separate entry path from chat, but produces the same MealEntry output and follows the same review-before-save rules.

## Entry points
- Accessible from the main UI as a distinct action (separate from chat input)
- Can also be triggered from chat if the user's intent suggests structured entry would help

## Core flow

### Step 1 — Seed input
User types a loose description of the meal. Examples:
- "chicken sandwich"
- "pasta I made at home"
- "lunch at a restaurant, had a burger and fries"

No precision required. The AI works from whatever is provided.

### Step 2 — AI first-pass draft
AI generates a structured meal draft block by block:
- Meal name
- Estimated items and portions
- Calorie and macro estimates
- Explicit assumptions surfaced inline (e.g. "assuming restaurant portion", "estimating 2 cups pasta")

### Step 3 — Clarification loop (up to 3 rounds)
AI asks targeted follow-up questions when something materially affects the estimate. Examples:
- "Was this home cooked or from a restaurant?"
- "Roughly how much pasta — a small bowl or a large plate?"
- "Did you add any sauce or cheese?"
- "Do you have the nutritional label for any of the ingredients?"

Rules:
- Only ask what materially changes the estimate
- Never ask for grams, exact weights, or precise measurements
- Accept rough answers — "a big bowl" is a valid answer
- AI exits the loop early when confidence is sufficient
- Maximum 3 clarification rounds
- User can skip remaining clarifications at any point

### Step 4 — Stress test (optional, user-triggered)
User can manually trigger a stress test pass before confirming.

The stress test sends the drafted meal back through the AI with a critical review prompt. It returns:
- Weak assumptions flagged (e.g. "portion size not confirmed", "home cooking assumed but not stated")
- Suggested follow-up questions the user may want to address

From stress test results the user can:
- Refine — AI works the flagged issues back into the draft
- Ignore — proceed with the original draft as-is

The stress test is always optional. The user can confirm and save without running it.

### Step 5 — Review and confirm
Final meal structure is presented for review. User can:
- Edit any individual field
- Confirm and save
- Discard and start over

Confidence-based path:
- High-confidence, previously-seen meals show a lightweight fast-confirm (one tap)
- Low-confidence or new meals show the full review card

### Step 6 — Save
Follows the same save flow as all other entry paths:
- MealEntry saved to Room
- MealItem records saved
- DailyMetrics recomputed
- FoodReference write-back review card shown if edited values differ from stored reference

## Assumptions policy
- Restaurant meals: pessimistic calorie assumptions
- Home meals: more optimistic assumptions
- Unknown portions: default serving size applied
- Sauces, oils, extras: conservative calories added
- All assumptions are surfaced explicitly in the draft, never hidden

## What the card does not do
- Does not ask for grams or precise weights
- Does not require nutritional labels (but uses them if offered)
- Does not auto-save without user confirmation
- Does not bypass the review step
- Does not replace chat — it is a complementary entry path

## MealEntry sourceType
Meals created via the card use sourceType: CARD to distinguish them from TEXT, PHOTO, and MANUAL entries.
