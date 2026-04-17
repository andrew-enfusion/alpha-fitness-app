package com.andrewenfusion.alphafitness.navigation

import androidx.annotation.StringRes
import com.andrewenfusion.alphafitness.R

enum class AlphaFitnessTopLevelDestination(
    val route: String,
    val glyph: String,
    @StringRes val labelRes: Int,
) {
    Log(
        route = AlphaFitnessDestination.Log.route,
        glyph = "L",
        labelRes = R.string.nav_log,
    ),
    Calendar(
        route = AlphaFitnessDestination.Calendar.route,
        glyph = "C",
        labelRes = R.string.nav_calendar,
    ),
    Insights(
        route = AlphaFitnessDestination.Insights.route,
        glyph = "I",
        labelRes = R.string.nav_insights,
    ),
    Profile(
        route = AlphaFitnessDestination.Profile.route,
        glyph = "P",
        labelRes = R.string.nav_profile,
    ),
}
