package com.andrewenfusion.alphafitness.feature.log

sealed interface LogUiEvent {
    data class DraftChanged(
        val value: String,
    ) : LogUiEvent

    data object SubmitClicked : LogUiEvent

    data object RetryInterpretationClicked : LogUiEvent

    data object ConfirmSaveClicked : LogUiEvent
}
