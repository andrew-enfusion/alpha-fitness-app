package com.andrewenfusion.alphafitness.feature.log.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import com.andrewenfusion.alphafitness.feature.log.LogOutputState
import com.andrewenfusion.alphafitness.feature.log.LogSaveState

@Composable
fun LogInterpretationStateCard(
    outputState: LogOutputState,
    saveState: LogSaveState,
    clarificationDraft: String,
    canRetryInterpretation: Boolean,
    canSubmitClarification: Boolean,
    canConfirmSave: Boolean,
    onRetryClicked: () -> Unit,
    onClarificationDraftChanged: (String) -> Unit,
    onClarificationOptionSelected: (String) -> Unit,
    onSubmitClarificationClicked: () -> Unit,
    onConfirmSaveClicked: () -> Unit,
) {
    AlphaFitnessSectionCard(
        title = stringResource(id = R.string.log_output_section_title),
        description = stringResource(id = R.string.log_output_section_description),
    ) {
        when (outputState) {
            LogOutputState.Empty -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = if (saveState is LogSaveState.Success) {
                            stringResource(
                                id = if (saveState.warningMessage == null) {
                                    R.string.log_save_success_title
                                } else {
                                    R.string.log_save_warning_title
                                },
                            )
                        } else if (saveState is LogSaveState.Failure) {
                            stringResource(id = R.string.log_save_error_title)
                        } else {
                            stringResource(id = R.string.log_output_empty_title)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = when (saveState) {
                            is LogSaveState.Success ->
                                if (saveState.warningMessage == null) {
                                    stringResource(
                                        id = R.string.log_save_success_body,
                                        saveState.savedMealId,
                                    )
                                } else {
                                    stringResource(
                                        id = R.string.log_save_warning_body,
                                        saveState.savedMealId,
                                        saveState.warningMessage,
                                    )
                                }
                            is LogSaveState.Failure -> saveState.message
                            else -> stringResource(id = R.string.log_output_empty_body)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (
                            saveState is LogSaveState.Failure ||
                            (saveState is LogSaveState.Success && saveState.warningMessage != null)
                        ) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
            LogOutputState.Loading -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(id = R.string.log_output_loading_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_output_loading_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            is LogOutputState.ValidationError -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_output_error_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = outputState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            is LogOutputState.InterpretationTimeout ->
                RetryableInterpretationFailureContent(
                    title = stringResource(id = R.string.log_output_timeout_title),
                    message = outputState.message,
                    canRetryInterpretation = canRetryInterpretation,
                    onRetryClicked = onRetryClicked,
                )
            is LogOutputState.InterpretationMalformed ->
                RetryableInterpretationFailureContent(
                    title = stringResource(id = R.string.log_output_malformed_title),
                    message = outputState.message,
                    canRetryInterpretation = canRetryInterpretation,
                    onRetryClicked = onRetryClicked,
                )
            is LogOutputState.InterpretationFailure ->
                RetryableInterpretationFailureContent(
                    title = stringResource(id = R.string.log_output_interpretation_error_title),
                    message = outputState.message,
                    canRetryInterpretation = canRetryInterpretation,
                    onRetryClicked = onRetryClicked,
                )
            is LogOutputState.LowConfidence -> {
                LogClarificationCard(
                    clarificationState = outputState.clarificationState,
                    clarificationDraft = clarificationDraft,
                    canSubmitClarification = canSubmitClarification,
                    onClarificationDraftChanged = onClarificationDraftChanged,
                    onClarificationOptionSelected = onClarificationOptionSelected,
                    onSubmitClarificationClicked = onSubmitClarificationClicked,
                )
            }
            is LogOutputState.ReviewReady -> {
                LogReviewCard(
                    reviewState = outputState.reviewState,
                    saveState = saveState,
                    canConfirmSave = canConfirmSave,
                    onConfirmSaveClicked = onConfirmSaveClicked,
                )
            }
        }
    }
}

@Composable
private fun RetryableInterpretationFailureContent(
    title: String,
    message: String,
    canRetryInterpretation: Boolean,
    onRetryClicked: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Button(
            onClick = onRetryClicked,
            enabled = canRetryInterpretation,
            modifier = Modifier,
        ) {
            Text(text = stringResource(id = R.string.log_output_retry))
        }
    }
}
