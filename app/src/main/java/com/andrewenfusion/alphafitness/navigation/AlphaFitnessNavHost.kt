package com.andrewenfusion.alphafitness.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.andrewenfusion.alphafitness.feature.home.HomeRoute

@Composable
fun AlphaFitnessNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AlphaFitnessDestination.Home.route,
        modifier = modifier,
    ) {
        composable(AlphaFitnessDestination.Home.route) {
            HomeRoute()
        }
    }
}
