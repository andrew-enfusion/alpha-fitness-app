package com.andrewenfusion.alphafitness.feature.appentry

data class AppEntryUiState(
    val isLoading: Boolean = true,
    val destination: AppEntryDestination? = null,
)

enum class AppEntryDestination {
    ONBOARDING,
    MAIN_SHELL,
}
