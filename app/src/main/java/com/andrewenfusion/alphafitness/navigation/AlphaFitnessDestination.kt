package com.andrewenfusion.alphafitness.navigation

sealed class AlphaFitnessDestination(
    val route: String,
) {
    data object Entry : AlphaFitnessDestination(route = "entry")
    data object Onboarding : AlphaFitnessDestination(route = "onboarding")
    data object ProfileEditor : AlphaFitnessDestination(route = "profile-editor")
    data object MainShell : AlphaFitnessDestination(route = "main-shell")
    data object Log : AlphaFitnessDestination(route = "log")
    data object Calendar : AlphaFitnessDestination(route = "calendar")
    data object Insights : AlphaFitnessDestination(route = "insights")
    data object Profile : AlphaFitnessDestination(route = "profile")
    data object CalendarDayDetail : AlphaFitnessDestination(route = "calendar/day-detail/{dateLabel}") {
        const val dateLabelArg: String = "dateLabel"

        fun createRoute(dateLabel: String): String = "calendar/day-detail/$dateLabel"
    }
}
