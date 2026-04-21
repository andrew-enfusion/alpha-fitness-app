package com.andrewenfusion.alphafitness.feature.log

data class LogUiState(
    val draftMessage: String = "",
    val outputState: LogOutputState = LogOutputState.Empty,
    val saveState: LogSaveState = LogSaveState.Idle,
) {
    val canSubmit: Boolean
        get() = draftMessage.isNotBlank() &&
            outputState != LogOutputState.Loading &&
            saveState != LogSaveState.Saving

    val canConfirmSave: Boolean
        get() = outputState is LogOutputState.ReviewReady &&
            saveState != LogSaveState.Saving
}
