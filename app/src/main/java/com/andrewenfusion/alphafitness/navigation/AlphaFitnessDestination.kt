package com.andrewenfusion.alphafitness.navigation

sealed class AlphaFitnessDestination(
    val route: String,
) {
    data object Onboarding : AlphaFitnessDestination(route = "onboarding")
    data object Home : AlphaFitnessDestination(route = "home")
}
