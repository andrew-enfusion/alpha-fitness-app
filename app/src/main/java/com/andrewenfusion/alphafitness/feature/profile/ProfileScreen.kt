package com.andrewenfusion.alphafitness.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessHeroCard
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessScreenScaffold
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
) {
    AlphaFitnessScreenScaffold(
        title = stringResource(id = R.string.profile_title),
        subtitle = stringResource(id = R.string.profile_subtitle),
        actionLabel = stringResource(id = R.string.profile_edit_action),
        onActionClick = onEditProfile,
    ) {
        if (uiState.isLoading) {
            item {
                CircularProgressIndicator()
            }
            return@AlphaFitnessScreenScaffold
        }

        item {
            AlphaFitnessHeroCard(
                label = stringResource(id = R.string.profile_summary_label),
                title = stringResource(id = R.string.profile_summary_title),
                body = stringResource(
                    id = R.string.profile_summary_body,
                    uiState.workingTarget,
                    uiState.baselineTarget,
                ),
                supportingText = uiState.explanation,
            )
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.profile_details_section_title),
                description = stringResource(id = R.string.profile_details_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    ProfileRow(
                        label = stringResource(id = R.string.profile_age_label),
                        value = uiState.age,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_sex_label),
                        value = uiState.sex,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_height_label),
                        value = uiState.height,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_weight_label),
                        value = uiState.weight,
                    )
                }
            }
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.profile_guidance_section_title),
                description = stringResource(id = R.string.profile_guidance_section_description),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                    ProfileRow(
                        label = stringResource(id = R.string.profile_exercise_label),
                        value = uiState.exerciseLevel,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_job_activity_label),
                        value = uiState.jobActivityLevel,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_goal_label),
                        value = uiState.goalType,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_baseline_target_label),
                        value = uiState.baselineTarget,
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.profile_working_target_label),
                        value = uiState.workingTarget,
                    )
                    uiState.proteinRange?.let { value ->
                        ProfileRow(
                            label = stringResource(id = R.string.profile_protein_label),
                            value = value,
                        )
                    }
                    uiState.carbRange?.let { value ->
                        ProfileRow(
                            label = stringResource(id = R.string.profile_carb_label),
                            value = value,
                        )
                    }
                    uiState.fatRange?.let { value ->
                        ProfileRow(
                            label = stringResource(id = R.string.profile_fat_label),
                            value = value,
                        )
                    }
                }
            }
        }

        item {
            AlphaFitnessSectionCard(
                title = stringResource(id = R.string.profile_actions_section_title),
                description = stringResource(id = R.string.profile_actions_section_description),
            ) {
                Button(
                    onClick = onEditProfile,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(id = R.string.profile_edit_button))
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.xSmall)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
