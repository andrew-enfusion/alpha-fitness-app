package com.andrewenfusion.alphafitness.feature.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.usecase.LogComposerSubmissionResult
import com.andrewenfusion.alphafitness.domain.usecase.InterpretLogMealUseCase
import com.andrewenfusion.alphafitness.domain.usecase.PrepareLogComposerSubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LogViewModel @Inject constructor(
    private val dispatchers: AppDispatchers,
    private val prepareLogComposerSubmissionUseCase: PrepareLogComposerSubmissionUseCase,
    private val interpretLogMealUseCase: InterpretLogMealUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = mutableUiState.asStateFlow()

    fun onEvent(event: LogUiEvent) {
        when (event) {
            is LogUiEvent.DraftChanged -> onDraftChanged(event.value)
            LogUiEvent.SubmitClicked -> onSubmitClicked()
            LogUiEvent.RetryInterpretationClicked -> onRetryInterpretationClicked()
        }
    }

    private fun onDraftChanged(value: String) {
        mutableUiState.update { currentState ->
            currentState.copy(
                draftMessage = value,
                outputState = if (
                    (
                        currentState.outputState is LogOutputState.ValidationError ||
                            currentState.outputState is LogOutputState.InterpretationError
                        ) &&
                    value.isNotBlank()
                ) {
                    LogOutputState.Empty
                } else {
                    currentState.outputState
                },
            )
        }
    }

    private fun onSubmitClicked() {
        when (val result = prepareLogComposerSubmissionUseCase(uiState.value.draftMessage)) {
            is LogComposerSubmissionResult.PendingSubmission -> {
                interpretSubmittedText(result.submittedText)
            }
            is LogComposerSubmissionResult.ValidationError -> {
                mutableUiState.update {
                    it.copy(
                        outputState = LogOutputState.ValidationError(
                            message = result.message,
                        ),
                    )
                }
            }
        }
    }

    private fun onRetryInterpretationClicked() {
        val currentOutputState = uiState.value.outputState
        val currentDraft = uiState.value.draftMessage

        when {
            currentOutputState is LogOutputState.InterpretationError && currentDraft.isNotBlank() ->
                interpretSubmittedText(currentDraft.trim())
            currentOutputState is LogOutputState.ValidationError && currentDraft.isNotBlank() ->
                interpretSubmittedText(currentDraft.trim())
            else -> Unit
        }
    }

    private fun interpretSubmittedText(submittedText: String) {
        viewModelScope.launch(dispatchers.main) {
            mutableUiState.update {
                it.copy(
                    draftMessage = submittedText,
                    outputState = LogOutputState.Loading,
                )
            }

            when (val result = interpretLogMealUseCase(submittedText)) {
                is AppResult.Success -> {
                    mutableUiState.update {
                        it.copy(
                            draftMessage = "",
                            outputState = LogOutputState.ReviewReady(
                                reviewState = result.value,
                            ),
                        )
                    }
                }
                is AppResult.Failure -> {
                    mutableUiState.update {
                        it.copy(
                            draftMessage = submittedText,
                            outputState = LogOutputState.InterpretationError(
                                message = result.error.message,
                            ),
                        )
                    }
                }
            }
        }
    }
}
