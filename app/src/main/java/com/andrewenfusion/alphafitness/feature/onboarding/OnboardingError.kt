package com.andrewenfusion.alphafitness.feature.onboarding

sealed interface OnboardingError {
    data object InvalidNumericInput : OnboardingError

    data class Message(
        val value: String,
    ) : OnboardingError
}
