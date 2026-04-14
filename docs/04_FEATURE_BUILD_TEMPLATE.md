# 04_FEATURE_BUILD_TEMPLATE.md

## Purpose
Define the minimum implementation pattern for every feature.

## Required structure
Each feature should include:
- UI screen(s)
- ViewModel
- UiState model
- UiEvent model if needed
- UseCase(s)
- Repository usage only through abstractions

## ViewModel rules
- No DAO access
- No AI provider access
- No business logic beyond orchestration of state and events
- Calls use cases only

## UseCase rules
- Single responsibility
- Business logic lives here or in domain engines/helpers
- No Android UI dependencies

## Repository rules
- Own persistence access
- Own AI gateway access
- Own cache lookup sequencing
- Return domain-safe models

## Compose UI rules
- Stateless where practical
- Driven by UiState
- No persistence or AI calls
- No calorie or macro business logic in composables

## Feature completion checklist
- Follows architecture guide
- Uses required boundaries
- Updates metrics when data changes
- Handles loading, success, and error states
- Adds or updates tests where relevant
- Updates implementation status
- Logs bugs if discovered
