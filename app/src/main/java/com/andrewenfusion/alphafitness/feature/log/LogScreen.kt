package com.andrewenfusion.alphafitness.feature.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessHeroCard
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessScreenScaffold
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import com.andrewenfusion.alphafitness.feature.log.component.LogComposerCard
import com.andrewenfusion.alphafitness.feature.log.component.LogInterpretationStateCard

@Composable
fun LogScreen(
    uiState: LogUiState,
    onDraftChanged: (String) -> Unit,
    onSubmitClicked: () -> Unit,
    onRetryInterpretationClicked: () -> Unit,
    onClarificationDraftChanged: (String) -> Unit,
    onClarificationOptionSelected: (String) -> Unit,
    onSubmitClarificationClicked: () -> Unit,
    onConfirmSaveClicked: () -> Unit,
) {
    AlphaFitnessScreenScaffold(
        title = stringResource(id = R.string.log_title),
        subtitle = stringResource(id = R.string.log_subtitle),
    ) {
        item {
            AlphaFitnessHeroCard(
                label = stringResource(id = R.string.log_workspace_label),
                title = stringResource(id = R.string.log_workspace_title),
                body = stringResource(id = R.string.log_workspace_body),
                supportingText = stringResource(id = R.string.log_workspace_supporting),
            )
        }

        item {
            LogComposerCard(
                draftMessage = uiState.draftMessage,
                canSubmit = uiState.canSubmit,
                onDraftChanged = onDraftChanged,
                onSubmitClicked = onSubmitClicked,
            )
        }

        item {
            LogInterpretationStateCard(
                outputState = uiState.outputState,
                saveState = uiState.saveState,
                clarificationDraft = uiState.clarificationDraft,
                canRetryInterpretation = uiState.canRetryInterpretation,
                canSubmitClarification = uiState.canSubmitClarification,
                canConfirmSave = uiState.canConfirmSave,
                onRetryClicked = onRetryInterpretationClicked,
                onClarificationDraftChanged = onClarificationDraftChanged,
                onClarificationOptionSelected = onClarificationOptionSelected,
                onSubmitClarificationClicked = onSubmitClarificationClicked,
                onConfirmSaveClicked = onConfirmSaveClicked,
            )
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.log_next_section_title),
                description = stringResource(id = R.string.log_next_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_next_row_one),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_next_row_two),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
