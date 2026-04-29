package com.andrewenfusion.alphafitness.domain.model

data class LogMealInterpretationDraft(
    val reviewState: LogMealReviewState,
    val clarificationNeeded: Boolean,
    val clarificationQuestion: String? = null,
)
