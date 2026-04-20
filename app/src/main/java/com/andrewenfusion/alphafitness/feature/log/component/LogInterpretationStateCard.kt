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

@Composable
fun LogInterpretationStateCard(
    outputState: LogOutputState,
    onRetryClicked: () -> Unit,
) {
    AlphaFitnessSectionCard(
        title = stringResource(id = R.string.log_output_section_title),
        description = stringResource(id = R.string.log_output_section_description),
    ) {
        when (outputState) {
            LogOutputState.Empty -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_output_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_output_empty_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            is LogOutputState.InterpretationError -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_output_interpretation_error_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = outputState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(
                        onClick = onRetryClicked,
                        modifier = Modifier,
                    ) {
                        Text(text = stringResource(id = R.string.log_output_retry))
                    }
                }
            }
            is LogOutputState.ReviewReady -> {
                LogReviewCard(
                    reviewState = outputState.reviewState,
                )
            }
        }
    }
}
