package com.andrewenfusion.alphafitness.navigation

sealed class AlphaFitnessDestination(
    val route: String,
) {
    data object Home : AlphaFitnessDestination(route = "home")
}
