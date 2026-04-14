package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    OnboardingScreen(
        uiState = uiState.value,
        onAgeChanged = viewModel::onAgeChanged,
        onHeightChanged = viewModel::onHeightChanged,
        onWeightChanged = viewModel::onWeightChanged,
        onSexSelected = viewModel::onSexSelected,
        onExerciseLevelSelected = viewModel::onExerciseLevelSelected,
        onJobActivityLevelSelected = viewModel::onJobActivityLevelSelected,
        onGoalTypeSelected = viewModel::onGoalTypeSelected,
        onSaveClicked = viewModel::onSaveClicked,
    )
}
