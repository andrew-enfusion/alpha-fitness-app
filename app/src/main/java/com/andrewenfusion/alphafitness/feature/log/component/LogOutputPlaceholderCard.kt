package com.andrewenfusion.alphafitness.feature.log.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import com.andrewenfusion.alphafitness.feature.log.LogOutputPlaceholderState

@Composable
fun LogOutputPlaceholderCard(
    outputPlaceholderState: LogOutputPlaceholderState,
) {
    AlphaFitnessSectionCard(
        title = stringResource(id = R.string.log_output_section_title),
        description = stringResource(id = R.string.log_output_section_description),
    ) {
        when (outputPlaceholderState) {
            LogOutputPlaceholderState.Empty -> {
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
            is LogOutputPlaceholderState.PendingSubmission -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_output_pending_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = outputPlaceholderState.submittedText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_output_pending_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            LogOutputPlaceholderState.ValidationError -> {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_output_error_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = stringResource(id = R.string.log_output_error_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
