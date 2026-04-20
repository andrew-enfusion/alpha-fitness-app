package com.andrewenfusion.alphafitness.domain.usecase

import javax.inject.Inject

class PrepareLogComposerSubmissionUseCase @Inject constructor() {
    operator fun invoke(rawDraft: String): LogComposerSubmissionResult {
        val normalizedDraft = rawDraft.trim()

        return if (normalizedDraft.isBlank()) {
            LogComposerSubmissionResult.ValidationError
        } else {
            LogComposerSubmissionResult.PendingSubmission(
                submittedText = normalizedDraft,
            )
        }
    }
}

sealed interface LogComposerSubmissionResult {
    data class PendingSubmission(
        val submittedText: String,
    ) : LogComposerSubmissionResult

    data object ValidationError : LogComposerSubmissionResult
}
