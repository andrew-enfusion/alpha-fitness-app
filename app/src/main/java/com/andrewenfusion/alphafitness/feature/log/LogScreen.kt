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

@Composable
fun LogScreen(
    uiState: LogUiState,
) {
    AlphaFitnessScreenScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
    ) {
        item {
            AlphaFitnessHeroCard(
                label = stringResource(id = R.string.log_placeholder_label),
                title = stringResource(id = R.string.log_placeholder_title),
                body = stringResource(id = R.string.log_placeholder_body),
                supportingText = stringResource(id = R.string.log_placeholder_supporting),
            )
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.log_workspace_section_title),
                description = stringResource(id = R.string.log_workspace_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_workspace_row_one),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_workspace_row_two),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.log_review_section_title),
                description = stringResource(id = R.string.log_review_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_review_row_one),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_review_row_two),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.log_memory_section_title),
                description = stringResource(id = R.string.log_memory_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.log_memory_row_one),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(id = R.string.log_memory_row_two),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
