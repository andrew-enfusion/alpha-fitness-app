package com.andrewenfusion.alphafitness.feature.log

import androidx.lifecycle.ViewModel
import com.andrewenfusion.alphafitness.domain.usecase.LogComposerSubmissionResult
import com.andrewenfusion.alphafitness.domain.usecase.PrepareLogComposerSubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class LogViewModel @Inject constructor(
    private val prepareLogComposerSubmissionUseCase: PrepareLogComposerSubmissionUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = mutableUiState.asStateFlow()

    fun onEvent(event: LogUiEvent) {
        when (event) {
            is LogUiEvent.DraftChanged -> onDraftChanged(event.value)
            LogUiEvent.SubmitClicked -> onSubmitClicked()
        }
    }

    private fun onDraftChanged(value: String) {
        mutableUiState.update { currentState ->
            currentState.copy(
                draftMessage = value,
                outputPlaceholderState = if (
                    currentState.outputPlaceholderState is LogOutputPlaceholderState.ValidationError &&
                    value.isNotBlank()
                ) {
                    LogOutputPlaceholderState.Empty
                } else {
                    currentState.outputPlaceholderState
                },
            )
        }
    }

    private fun onSubmitClicked() {
        when (val result = prepareLogComposerSubmissionUseCase(uiState.value.draftMessage)) {
            is LogComposerSubmissionResult.PendingSubmission -> {
                mutableUiState.update {
                    it.copy(
                        draftMessage = "",
                        outputPlaceholderState = LogOutputPlaceholderState.PendingSubmission(
                            submittedText = result.submittedText,
                        ),
                    )
                }
            }
            LogComposerSubmissionResult.ValidationError -> {
                mutableUiState.update {
                    it.copy(
                        outputPlaceholderState = LogOutputPlaceholderState.ValidationError,
                    )
                }
            }
        }
    }
}
