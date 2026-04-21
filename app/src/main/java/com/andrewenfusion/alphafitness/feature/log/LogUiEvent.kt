package com.andrewenfusion.alphafitness.feature.log

sealed interface LogUiEvent {
    data class DraftChanged(
        val value: String,
    ) : LogUiEvent

    data object SubmitClicked : LogUiEvent

    data object RetryInterpretationClicked : LogUiEvent

    data class ClarificationDraftChanged(
        val value: String,
    ) : LogUiEvent

    data class ClarificationOptionSelected(
        val value: String,
    ) : LogUiEvent

    data object SubmitClarificationClicked : LogUiEvent

    data object ConfirmSaveClicked : LogUiEvent
}
