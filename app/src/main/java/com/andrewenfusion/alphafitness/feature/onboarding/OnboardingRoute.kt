package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingRoute(
    mode: OnboardingMode = OnboardingMode.FirstRun,
    onCompleted: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value.saveSucceeded) {
        if (uiState.value.saveSucceeded) {
            onCompleted()
        }
    }

    OnboardingScreen(
        mode = mode,
        uiState = uiState.value,
        onNavigateBack = onNavigateBack,
        onAgeChanged = viewModel::onAgeChanged,
        onHeightChanged = viewModel::onHeightChanged,
        onWeightChanged = viewModel::onWeightChanged,
        onSexSelected = viewModel::onSexSelected,
        onExerciseLevelSelected = viewModel::onExerciseLevelSelected,
        onJobActivityLevelSelected = viewModel::onJobActivityLevelSelected,
        onGoalTypeSelected = viewModel::onGoalTypeSelected,
        onSaveClicked = viewModel::onSaveClicked,
        onRetryGuidanceClicked = viewModel::onRetryGuidanceClicked,
        onResetWorkingTargetClicked = viewModel::onResetWorkingTargetClicked,
    )
}
