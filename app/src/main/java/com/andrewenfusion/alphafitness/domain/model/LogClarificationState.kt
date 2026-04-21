package com.andrewenfusion.alphafitness.domain.model

data class LogClarificationState(
    val originalSubmittedDraft: String,
    val question: String,
    val quickOptions: List<LogClarificationOption>,
    val partialReviewState: LogMealReviewState? = null,
)
