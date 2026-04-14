package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onAgeChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onSexSelected: (Sex) -> Unit,
    onExerciseLevelSelected: (ExerciseLevel) -> Unit,
    onJobActivityLevelSelected: (JobActivityLevel) -> Unit,
    onGoalTypeSelected: (GoalType) -> Unit,
    onSaveClicked: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(text = stringResource(id = R.string.onboarding_title))
                },
            )
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(AlphaFitnessSpacing.large),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                )
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AlphaFitnessSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.medium),
            ) {
                item {
                    OnboardingSectionCard(
                        title = stringResource(id = R.string.onboarding_subtitle),
                        description = stringResource(id = R.string.onboarding_helper_message),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small)) {
                            Text(
                                text = stringResource(id = R.string.onboarding_phase_badge),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = stringResource(id = R.string.onboarding_intro_title),
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(id = R.string.onboarding_intro_body),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                item {
                    OnboardingSectionCard(
                        title = stringResource(id = R.string.onboarding_measurements_section),
                        description = stringResource(id = R.string.onboarding_measurements_description),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.medium)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.small),
                            ) {
                                OnboardingNumberField(
                                    label = stringResource(id = R.string.onboarding_age_label),
                                    value = uiState.age,
                                    modifier = Modifier.weight(1f),
                                    onValueChanged = onAgeChanged,
                                )
                                OnboardingNumberField(
                                    label = stringResource(id = R.string.onboarding_height_label),
                                    value = uiState.heightCm,
                                    modifier = Modifier.weight(1f),
                                    onValueChanged = onHeightChanged,
                                )
                            }

                            OnboardingNumberField(
                                label = stringResource(id = R.string.onboarding_weight_label),
                                value = uiState.weightKg,
                                modifier = Modifier.fillMaxWidth(),
                                onValueChanged = onWeightChanged,
                            )
                        }
                    }
                }

                item {
                    OnboardingChoiceGroup(
                        title = stringResource(id = R.string.onboarding_sex_label),
                        options = Sex.entries.filterNot { it == Sex.UNSPECIFIED },
                        selectedOption = uiState.sex,
                        onOptionSelected = onSexSelected,
                        optionLabelRes = ::sexLabelRes,
                    )
                }

                item {
                    OnboardingChoiceGroup(
                        title = stringResource(id = R.string.onboarding_exercise_label),
                        options = ExerciseLevel.entries.filterNot { it == ExerciseLevel.UNSPECIFIED },
                        selectedOption = uiState.exerciseLevel,
                        onOptionSelected = onExerciseLevelSelected,
                        optionLabelRes = ::exerciseLevelRes,
                    )
                }

                item {
                    OnboardingChoiceGroup(
                        title = stringResource(id = R.string.onboarding_job_activity_label),
                        options = JobActivityLevel.entries.filterNot { it == JobActivityLevel.UNSPECIFIED },
                        selectedOption = uiState.jobActivityLevel,
                        onOptionSelected = onJobActivityLevelSelected,
                        optionLabelRes = ::jobActivityLevelRes,
                    )
                }

                item {
                    OnboardingChoiceGroup(
                        title = stringResource(id = R.string.onboarding_goal_label),
                        options = GoalType.entries.filterNot { it == GoalType.UNSPECIFIED },
                        selectedOption = uiState.goalType,
                        onOptionSelected = onGoalTypeSelected,
                        optionLabelRes = ::goalTypeLabelRes,
                    )
                }

                if (uiState.calorieTarget != null) {
                    item {
                        OnboardingSectionCard(
                            title = stringResource(id = R.string.onboarding_target_section),
                            description = stringResource(id = R.string.onboarding_target_description),
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.onboarding_target_value,
                                    uiState.calorieTarget,
                                ),
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = stringResource(id = R.string.onboarding_target_formula_note),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (uiState.error != null) {
                    item {
                        OnboardingStatusCard(
                            text = onboardingErrorText(error = uiState.error),
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                if (uiState.saveSucceeded) {
                    item {
                        OnboardingStatusCard(
                            text = stringResource(id = R.string.onboarding_saved_message),
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                item {
                    OnboardingSectionCard(
                        title = stringResource(id = R.string.onboarding_action_section),
                        description = stringResource(id = R.string.onboarding_action_description),
                    ) {
                        Button(
                            onClick = onSaveClicked,
                            enabled = uiState.canSave,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            Text(
                                text = if (uiState.isSaving) {
                                    stringResource(id = R.string.onboarding_saving_label)
                                } else {
                                    stringResource(id = R.string.onboarding_save_label)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun onboardingErrorText(error: OnboardingError): String =
    when (error) {
        OnboardingError.InvalidNumericInput -> stringResource(id = R.string.onboarding_invalid_numeric_message)
        is OnboardingError.Message -> error.value
    }

private fun sexLabelRes(value: Sex): Int =
    when (value) {
        Sex.FEMALE -> R.string.onboarding_sex_female
        Sex.MALE -> R.string.onboarding_sex_male
        Sex.UNSPECIFIED -> R.string.onboarding_option_unspecified
    }

private fun exerciseLevelRes(value: ExerciseLevel): Int =
    when (value) {
        ExerciseLevel.LOW -> R.string.onboarding_exercise_low
        ExerciseLevel.MODERATE -> R.string.onboarding_exercise_moderate
        ExerciseLevel.HIGH -> R.string.onboarding_exercise_high
        ExerciseLevel.ATHLETE -> R.string.onboarding_exercise_athlete
        ExerciseLevel.UNSPECIFIED -> R.string.onboarding_option_unspecified
    }

private fun jobActivityLevelRes(value: JobActivityLevel): Int =
    when (value) {
        JobActivityLevel.SEDENTARY -> R.string.onboarding_job_sedentary
        JobActivityLevel.LIGHT -> R.string.onboarding_job_light
        JobActivityLevel.ACTIVE -> R.string.onboarding_job_active
        JobActivityLevel.VERY_ACTIVE -> R.string.onboarding_job_very_active
        JobActivityLevel.UNSPECIFIED -> R.string.onboarding_option_unspecified
    }

private fun goalTypeLabelRes(value: GoalType): Int =
    when (value) {
        GoalType.LOSE -> R.string.onboarding_goal_lose
        GoalType.MAINTAIN -> R.string.onboarding_goal_maintain
        GoalType.GAIN -> R.string.onboarding_goal_gain
        GoalType.UNSPECIFIED -> R.string.onboarding_option_unspecified
    }
