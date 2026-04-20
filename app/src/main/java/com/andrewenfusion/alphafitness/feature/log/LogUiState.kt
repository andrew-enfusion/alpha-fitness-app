package com.andrewenfusion.alphafitness.feature.log

data class LogUiState(
    val draftMessage: String = "",
    val outputPlaceholderState: LogOutputPlaceholderState = LogOutputPlaceholderState.Empty,
) {
    val canSubmit: Boolean
        get() = draftMessage.isNotBlank()
}
