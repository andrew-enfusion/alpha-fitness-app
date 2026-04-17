package com.andrewenfusion.alphafitness.feature.insights

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
fun InsightsScreen(
    uiState: InsightsUiState,
) {
    AlphaFitnessScreenScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
    ) {
        item {
            AlphaFitnessHeroCard(
                label = stringResource(id = R.string.insights_placeholder_label),
                title = stringResource(id = R.string.insights_placeholder_title),
                body = stringResource(id = R.string.insights_placeholder_body),
                supportingText = stringResource(id = R.string.insights_placeholder_supporting),
            )
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.insights_section_one_title),
                description = stringResource(id = R.string.insights_section_one_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.insights_section_one_row),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.insights_section_two_title),
                description = stringResource(id = R.string.insights_section_two_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(id = R.string.insights_section_two_row),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
