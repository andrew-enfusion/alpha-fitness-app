package com.andrewenfusion.alphafitness.feature.log.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.feature.log.LogSaveState

@Composable
fun LogReviewCard(
    reviewState: LogMealReviewState,
    saveState: LogSaveState,
    canConfirmSave: Boolean,
    onConfirmSaveClicked: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.medium)) {
        Text(
            text = stringResource(id = R.string.log_review_ready_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        SourceBadge(source = reviewState.interpretationSource)

        Text(
            text = reviewState.submittedText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        reviewState.items.forEach { item ->
            ReviewItemRow(item = item)
        }

        Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.xSmall)) {
            Text(
                text = stringResource(
                    id = R.string.log_review_totals,
                    reviewState.totalCalories,
                    reviewState.totalProtein,
                    reviewState.totalCarbs,
                    reviewState.totalFat,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(id = R.string.log_review_requires_review),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        if (reviewState.assumptions.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.xSmall)) {
                Text(
                    text = stringResource(id = R.string.log_review_assumptions_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                reviewState.assumptions.forEach { assumption ->
                    Text(
                        text = stringResource(id = R.string.log_review_assumption_bullet, assumption),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Button(
            onClick = onConfirmSaveClicked,
            enabled = canConfirmSave,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (saveState == LogSaveState.Saving) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(
                        id = if (saveState is LogSaveState.Failure) {
                            R.string.log_review_retry_save
                        } else {
                            R.string.log_review_confirm_save
                        },
                    ),
                )
            }
        }

        if (saveState is LogSaveState.Failure) {
            Text(
                text = saveState.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun SourceBadge(
    source: LogMealInterpretationSource,
) {
    val label = when (source) {
        LogMealInterpretationSource.LOCAL_MATCH -> stringResource(id = R.string.log_review_source_local)
        LogMealInterpretationSource.AI_FALLBACK -> stringResource(id = R.string.log_review_source_ai)
    }

    Text(
        text = label,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(
                horizontal = AlphaFitnessSpacing.small,
                vertical = AlphaFitnessSpacing.xSmall,
            ),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ReviewItemRow(
    item: LogMealReviewItem,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(AlphaFitnessSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.xSmall),
    ) {
        Text(
            text = item.displayName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = item.portionDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.log_review_item_calories, item.calories),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(
                    id = R.string.log_review_item_macros,
                    item.protein,
                    item.carbs,
                    item.fat,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
