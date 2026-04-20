package com.andrewenfusion.alphafitness.feature.log

import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState

sealed interface LogOutputState {
    data object Empty : LogOutputState

    data object Loading : LogOutputState

    data class ValidationError(
        val message: String,
    ) : LogOutputState

    data class InterpretationError(
        val message: String,
    ) : LogOutputState

    data class ReviewReady(
        val reviewState: LogMealReviewState,
    ) : LogOutputState
}
