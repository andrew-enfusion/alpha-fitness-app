package com.andrewenfusion.alphafitness.feature.log

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LogRoute(
    viewModel: LogViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LogScreen(
        uiState = uiState,
        onDraftChanged = { value ->
            viewModel.onEvent(LogUiEvent.DraftChanged(value))
        },
        onSubmitClicked = {
            viewModel.onEvent(LogUiEvent.SubmitClicked)
        },
        onRetryInterpretationClicked = {
            viewModel.onEvent(LogUiEvent.RetryInterpretationClicked)
        },
        onClarificationDraftChanged = { value ->
            viewModel.onEvent(LogUiEvent.ClarificationDraftChanged(value))
        },
        onClarificationOptionSelected = { value ->
            viewModel.onEvent(LogUiEvent.ClarificationOptionSelected(value))
        },
        onSubmitClarificationClicked = {
            viewModel.onEvent(LogUiEvent.SubmitClarificationClicked)
        },
        onConfirmSaveClicked = {
            viewModel.onEvent(LogUiEvent.ConfirmSaveClicked)
        },
    )
}
