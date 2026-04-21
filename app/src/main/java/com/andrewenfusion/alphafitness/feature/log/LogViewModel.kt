package com.andrewenfusion.alphafitness.feature.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.usecase.ConfirmLogMealSaveOutcome
import com.andrewenfusion.alphafitness.domain.usecase.ConfirmLogMealSaveUseCase
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
    private val confirmLogMealSaveUseCase: ConfirmLogMealSaveUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = mutableUiState.asStateFlow()

    fun onEvent(event: LogUiEvent) {
        when (event) {
            is LogUiEvent.DraftChanged -> onDraftChanged(event.value)
            is LogUiEvent.ClarificationDraftChanged -> onClarificationDraftChanged(event.value)
            is LogUiEvent.ClarificationOptionSelected -> onClarificationSubmitted(event.value)
            LogUiEvent.SubmitClicked -> onSubmitClicked()
            LogUiEvent.SubmitClarificationClicked -> onSubmitClarificationClicked()
            LogUiEvent.RetryInterpretationClicked -> onRetryInterpretationClicked()
            LogUiEvent.ConfirmSaveClicked -> onConfirmSaveClicked()
        }
    }

    private fun onDraftChanged(value: String) {
        mutableUiState.update { currentState ->
            currentState.copy(
                draftMessage = value,
                saveState = if (currentState.saveState != LogSaveState.Idle) {
                    LogSaveState.Idle
                } else {
                    currentState.saveState
                },
                clarificationDraft = if (currentState.outputState is LogOutputState.LowConfidence) {
                    currentState.clarificationDraft
                } else {
                    ""
                },
                outputState = if (
                    currentState.outputState is LogOutputState.ValidationError &&
                    value.isNotBlank()
                ) {
                    LogOutputState.Empty
                } else {
                    currentState.outputState
                },
            )
        }
    }

    private fun onClarificationDraftChanged(value: String) {
        mutableUiState.update { currentState ->
            currentState.copy(
                clarificationDraft = value,
                saveState = if (currentState.saveState != LogSaveState.Idle) {
                    LogSaveState.Idle
                } else {
                    currentState.saveState
                },
            )
        }
    }

    private fun onSubmitClicked() {
        mutableUiState.update { it.copy(saveState = LogSaveState.Idle) }
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
        val submittedDraft = uiState.value.submittedDraft
        if (uiState.value.canRetryInterpretation && !submittedDraft.isNullOrBlank()) {
            interpretSubmittedText(originalSubmittedDraft = submittedDraft)
        }
    }

    private fun onSubmitClarificationClicked() {
        val clarificationAnswer = uiState.value.clarificationDraft.trim()
        if (clarificationAnswer.isNotBlank()) {
            onClarificationSubmitted(clarificationAnswer)
        }
    }

    private fun onClarificationSubmitted(
        clarificationAnswer: String,
    ) {
        val originalSubmittedDraft = uiState.value.submittedDraft
        if (
            uiState.value.outputState is LogOutputState.LowConfidence &&
            !originalSubmittedDraft.isNullOrBlank()
        ) {
            interpretSubmittedText(
                originalSubmittedDraft = originalSubmittedDraft,
                clarificationAnswer = clarificationAnswer,
            )
        }
    }

    private fun interpretSubmittedText(
        originalSubmittedDraft: String,
        clarificationAnswer: String? = null,
    ) {
        viewModelScope.launch(dispatchers.main) {
            mutableUiState.update {
                it.copy(
                    submittedDraft = originalSubmittedDraft,
                    clarificationDraft = "",
                    saveState = LogSaveState.Idle,
                    outputState = LogOutputState.Loading,
                )
            }

            when (val result = interpretLogMealUseCase(originalSubmittedDraft, clarificationAnswer)) {
                is AppResult.Success -> {
                    mutableUiState.update {
                        val nextOutputState = result.value
                        it.copy(
                            draftMessage = if (
                                clarificationAnswer == null &&
                                it.draftMessage.trim() == originalSubmittedDraft
                            ) {
                                ""
                            } else {
                                it.draftMessage
                            },
                            clarificationDraft = "",
                            saveState = LogSaveState.Idle,
                            outputState = nextOutputState,
                        )
                    }
                }
                is AppResult.Failure -> {
                    mutableUiState.update {
                        it.copy(
                            clarificationDraft = "",
                            saveState = LogSaveState.Idle,
                            outputState = result.error.toInterpretationFailureState(),
                        )
                    }
                }
            }
        }
    }

    private fun onConfirmSaveClicked() {
        val reviewState = (uiState.value.outputState as? LogOutputState.ReviewReady)?.reviewState ?: return

        if (uiState.value.saveState == LogSaveState.Saving) {
            return
        }

        viewModelScope.launch(dispatchers.main) {
            mutableUiState.update { currentState ->
                currentState.copy(saveState = LogSaveState.Saving)
            }

            when (val result = confirmLogMealSaveUseCase(reviewState)) {
                is AppResult.Success -> {
                    when (val outcome = result.value) {
                        is ConfirmLogMealSaveOutcome.Success -> {
                            mutableUiState.update {
                                it.copy(
                                    outputState = LogOutputState.Empty,
                                    saveState = LogSaveState.Success(
                                        savedMealId = outcome.savedMealId,
                                    ),
                                    clarificationDraft = "",
                                    submittedDraft = null,
                                )
                            }
                        }
                        is ConfirmLogMealSaveOutcome.MetricsRefreshFailed -> {
                            mutableUiState.update {
                                it.copy(
                                    outputState = LogOutputState.Empty,
                                    saveState = LogSaveState.Success(
                                        savedMealId = outcome.savedMealId,
                                        warningMessage = outcome.message,
                                    ),
                                    clarificationDraft = "",
                                    submittedDraft = null,
                                )
                            }
                        }
                    }
                }
                is AppResult.Failure -> {
                    mutableUiState.update {
                        it.copy(
                            saveState = LogSaveState.Failure(
                                message = result.error.message,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun AppError.toInterpretationFailureState(): LogOutputState =
        when (this) {
            is AppError.Validation -> LogOutputState.ValidationError(message = message)
            is AppError.AiTimeout -> LogOutputState.InterpretationTimeout(message = message)
            is AppError.AiMalformedResponse -> LogOutputState.InterpretationMalformed(message = message)
            is AppError.AiUnavailable,
            is AppError.NetworkUnavailable,
            is AppError.Storage,
            is AppError.Unknown,
            is AppError.Unsupported,
            -> LogOutputState.InterpretationFailure(message = message)
        }
}
