package com.andrewenfusion.alphafitness.feature.onboarding

sealed interface OnboardingGuidanceStatus {
    data object MatchesBaseline : OnboardingGuidanceStatus

    data object AdjustedWorkingTarget : OnboardingGuidanceStatus

    data object ResetToBaseline : OnboardingGuidanceStatus
}
