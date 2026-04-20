package com.andrewenfusion.alphafitness.domain.usecase

import javax.inject.Inject

class PrepareLogComposerSubmissionUseCase @Inject constructor() {
    operator fun invoke(rawDraft: String): LogComposerSubmissionResult {
        val normalizedDraft = rawDraft.trim()

        return if (normalizedDraft.isBlank()) {
            LogComposerSubmissionResult.ValidationError(
                message = "Enter a meal description first.",
            )
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

    data class ValidationError(
        val message: String,
    ) : LogComposerSubmissionResult
}
