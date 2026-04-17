# 01_PRODUCT_SPEC.md

## Purpose
Define the product scope, user value, and non-goals for Alpha Fitness App.

## Product summary
A single-user, local-first Android calorie tracking app with a visible AI assistant.

## Product information architecture
- Onboarding is a dedicated first-run setup flow and is separate from the main app shell.
- Log is the primary assisted meal-entry surface.
- Calendar is the date-browsing surface.
- Calendar Day Detail owns selected-day meal history and replaces any need for a separate top-level History tab.
- Insights is the summaries and trends surface.
- Profile is the user data, settings, and profile-update surface.

## Core user value
- Fast meal logging by text or photo
- Approximate calorie and macro tracking
- AI-guided onboarding and summaries
- Local nutrition memory that improves with reuse
- Calendar and insight views backed by structured data

## V1 goals
- AI-guided onboarding
- User profile persistence and updates
- Text-based meal logging on the Log destination
- AI-assisted meal card (separate structured entry path)
- Structured meal review before save
- Local meal history and editing through Calendar Day Detail
- Daily metrics and calendar view
- Weekly summaries
- Offline viewing of local data
- Manual fallback when AI is unavailable

## V1 non-goals
- Multi-profile support
- Hard macro goals in onboarding
- Precision micronutrient tracking
- Cloud-first sync
- Silent auto-save of AI meal interpretations
- Advanced provider routing

## Product rules
- Calories are the only explicit goal in V1.
- Macros are tracked as guidance, not explicit targets.
- Convenience beats precision.
- Consistency beats speculative intelligence.
- AI assists; the app owns the truth.
- User is never asked for grams, exact weights, or precise measurements.
- Reasonable assumptions are made and surfaced, not blocked on.
- The main post-onboarding app shell uses top-level destinations for Log, Calendar, Insights, and Profile only.
- History is not a top-level destination in V1; past-day meal browsing belongs under Calendar Day Detail.

## Primary user
Single phone owner using the app for personal calorie tracking, with future scalability possible but not exposed as multi-user UX in V1.
