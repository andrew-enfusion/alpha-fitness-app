package com.andrewenfusion.alphafitness.feature.log

data class LogUiState(
    val draftMessage: String = "",
    val outputState: LogOutputState = LogOutputState.Empty,
) {
    val canSubmit: Boolean
        get() = draftMessage.isNotBlank() && outputState != LogOutputState.Loading
}
