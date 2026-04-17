package com.andrewenfusion.alphafitness.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessHeroCard
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessScreenScaffold
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onOpenDayDetail: (String) -> Unit,
) {
    AlphaFitnessScreenScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
    ) {
        item {
            AlphaFitnessHeroCard(
                label = stringResource(id = R.string.calendar_placeholder_label),
                title = stringResource(id = R.string.calendar_placeholder_title),
                body = stringResource(id = R.string.calendar_placeholder_body),
                supportingText = stringResource(id = R.string.calendar_placeholder_supporting),
            )
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.calendar_browse_section_title),
                description = stringResource(id = R.string.calendar_browse_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.medium)) {
                    repeat(5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small),
                        ) {
                            repeat(7) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.calendar_day_detail_section_title),
                description = stringResource(id = R.string.calendar_day_detail_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    Text(
                        text = stringResource(
                            id = R.string.calendar_selected_day_value,
                            uiState.selectedDayLabel,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Button(
                        onClick = { onOpenDayDetail(uiState.selectedDayLabel) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(id = R.string.calendar_open_day_detail))
                    }
                }
            }
        }
    }
}
