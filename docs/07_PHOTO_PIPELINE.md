# 07_PHOTO_PIPELINE.md

## Purpose
Define the photo-based meal logging flow.

## Status
Photo logging is deferred until after stable text logging unless explicitly prioritized.

## Core rule
Photo logging must not bypass the same validation, review, and persistence rules as text logging.

## Proposed flow
1. Capture or select image
2. Send image through AI/vision interpretation path
3. Receive structured candidate items
4. Apply local lookup and normalization per item
5. Build review card
6. User confirms or edits
7. Save to Room
8. Recompute metrics

## Requirements
- Multi-item detection support
- Per-item confidence
- Portion assumptions must be explicit
- Review is mandatory in V1 photo flow

## Failure behavior
If image interpretation is too ambiguous:
- do not auto-save
- present best-effort suggestions only
- allow manual editing or manual entry fallback
