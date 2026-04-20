package com.andrewenfusion.alphafitness.feature.log

import com.andrewenfusion.alphafitness.domain.usecase.PrepareLogComposerSubmissionUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogViewModelTest {
    private val viewModel = LogViewModel(
        prepareLogComposerSubmissionUseCase = PrepareLogComposerSubmissionUseCase(),
    )

    @Test
    fun submitWithBlankDraftShowsValidationPlaceholder() {
        viewModel.onEvent(LogUiEvent.SubmitClicked)

        assertEquals(
            LogOutputPlaceholderState.ValidationError,
            viewModel.uiState.value.outputPlaceholderState,
        )
    }

    @Test
    fun submitWithDraftMovesTextIntoPendingOutputStateAndClearsComposer() {
        viewModel.onEvent(LogUiEvent.DraftChanged("Chicken burrito bowl and iced tea"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)

        assertEquals("", viewModel.uiState.value.draftMessage)
        assertEquals(
            LogOutputPlaceholderState.PendingSubmission(
                submittedText = "Chicken burrito bowl and iced tea",
            ),
            viewModel.uiState.value.outputPlaceholderState,
        )
    }

    @Test
    fun editingAfterValidationErrorClearsTheErrorPlaceholder() {
        viewModel.onEvent(LogUiEvent.SubmitClicked)
        viewModel.onEvent(LogUiEvent.DraftChanged("Greek yogurt with granola"))

        assertEquals(
            LogOutputPlaceholderState.Empty,
            viewModel.uiState.value.outputPlaceholderState,
        )
        assertTrue(viewModel.uiState.value.canSubmit)
    }
}
