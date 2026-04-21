package com.andrewenfusion.alphafitness.feature.log

data class LogUiState(
    val draftMessage: String = "",
    val submittedDraft: String? = null,
    val clarificationDraft: String = "",
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

    val canRetryInterpretation: Boolean
        get() = outputState.isRetryableInterpretationFailure &&
            !submittedDraft.isNullOrBlank() &&
            saveState != LogSaveState.Saving

    val canSubmitClarification: Boolean
        get() = outputState is LogOutputState.LowConfidence &&
            clarificationDraft.isNotBlank() &&
            saveState != LogSaveState.Saving
}
