package com.andrewenfusion.alphafitness.feature.log

import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState

sealed interface LogOutputState {
    data object Empty : LogOutputState

    data object Loading : LogOutputState

    data class ValidationError(
        val message: String,
    ) : LogOutputState

    data class InterpretationTimeout(
        val message: String,
    ) : LogOutputState

    data class InterpretationMalformed(
        val message: String,
    ) : LogOutputState

    data class InterpretationFailure(
        val message: String,
    ) : LogOutputState

    data class ReviewReady(
        val reviewState: LogMealReviewState,
    ) : LogOutputState
}

val LogOutputState.isRetryableInterpretationFailure: Boolean
    get() = this is LogOutputState.InterpretationTimeout ||
        this is LogOutputState.InterpretationMalformed ||
        this is LogOutputState.InterpretationFailure
