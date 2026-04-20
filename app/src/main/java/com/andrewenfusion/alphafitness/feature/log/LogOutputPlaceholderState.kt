package com.andrewenfusion.alphafitness.feature.log

sealed interface LogOutputPlaceholderState {
    data object Empty : LogOutputPlaceholderState

    data class PendingSubmission(
        val submittedText: String,
    ) : LogOutputPlaceholderState

    data object ValidationError : LogOutputPlaceholderState
}
