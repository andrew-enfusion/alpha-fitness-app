package com.andrewenfusion.alphafitness.feature.log.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import com.andrewenfusion.alphafitness.domain.model.LogClarificationState

@Composable
fun LogClarificationCard(
    clarificationState: LogClarificationState,
    clarificationDraft: String,
    canSubmitClarification: Boolean,
    onClarificationDraftChanged: (String) -> Unit,
    onClarificationOptionSelected: (String) -> Unit,
    onSubmitClarificationClicked: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.medium)) {
        Text(
            text = stringResource(id = R.string.log_clarification_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = clarificationState.question,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (clarificationState.partialReviewState != null) {
            val partialReviewState = clarificationState.partialReviewState
            Text(
                text = stringResource(
                    id = R.string.log_clarification_partial_summary,
                    partialReviewState.items.joinToString { it.displayName },
                    partialReviewState.totalCalories,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
            clarificationState.quickOptions.forEach { option ->
                Button(
                    onClick = { onClarificationOptionSelected(option.value) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = option.label)
                }
            }
        }

        OutlinedTextField(
            value = clarificationDraft,
            onValueChange = onClarificationDraftChanged,
            modifier = Modifier.fillMaxWidth(),
            minLines = 1,
            maxLines = 3,
            placeholder = {
                Text(text = stringResource(id = R.string.log_clarification_placeholder))
            },
            label = {
                Text(text = stringResource(id = R.string.log_clarification_label))
            },
        )

        Button(
            onClick = onSubmitClarificationClicked,
            enabled = canSubmitClarification,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(id = R.string.log_clarification_submit))
        }
    }
}
