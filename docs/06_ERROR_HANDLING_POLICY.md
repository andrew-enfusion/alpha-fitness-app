# 06_ERROR_HANDLING_POLICY.md

## Purpose
Define error handling, retry behavior, fallback behavior, and user-visible recovery rules.

## Error categories
- AI_TIMEOUT
- AI_UNAVAILABLE
- AI_MALFORMED_RESPONSE
- NETWORK_UNAVAILABLE
- DB_WRITE_FAILURE
- DB_READ_FAILURE
- VALIDATION_FAILURE
- UNKNOWN_ERROR

## Core policy
State-changing flows must fail safely. No partial state should be treated as committed unless persistence succeeds.

## AI failure behavior
When AI fails:
- do not create a meal automatically
- preserve user input draft if possible
- offer retry and manual entry fallback
- show clear user-facing explanation

## DB failure behavior
If AI succeeds but local persistence fails:
- do not mark the meal as saved
- show failure state
- preserve parsed review data temporarily if possible
- allow retry of save after issue resolution

## Network unavailable behavior
When offline:
- disable AI interpretation actions
- allow manual entry
- allow local browsing and editing
- show clear offline messaging

## Retry policy
- AI timeout: allow user-initiated retry
- malformed AI response: no blind retry loop; either retry once via controlled path or fall back to manual correction
- DB write failure: allow retry after surfacing the issue

## UI states
Every major flow should support:
- idle
- loading
- success
- recoverable error
- blocking error

## Logging rule
Errors affecting core flows should be recorded in bug tracking when reproducible or severe.
