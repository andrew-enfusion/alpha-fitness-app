package com.andrewenfusion.alphafitness.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.andrewenfusion.alphafitness.feature.appentry.AppEntryRoute
import com.andrewenfusion.alphafitness.feature.onboarding.OnboardingMode
import com.andrewenfusion.alphafitness.feature.onboarding.OnboardingRoute

@Composable
fun AlphaFitnessNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AlphaFitnessDestination.Entry.route,
        modifier = modifier,
    ) {
        composable(AlphaFitnessDestination.Entry.route) {
            AppEntryRoute(
                onNavigateToOnboarding = {
                    navController.navigate(AlphaFitnessDestination.Onboarding.route) {
                        popUpTo(AlphaFitnessDestination.Entry.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToMainShell = {
                    navController.navigate(AlphaFitnessDestination.MainShell.route) {
                        popUpTo(AlphaFitnessDestination.Entry.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(AlphaFitnessDestination.Onboarding.route) {
            OnboardingRoute(
                mode = OnboardingMode.FirstRun,
                onCompleted = {
                    navController.navigate(AlphaFitnessDestination.MainShell.route) {
                        popUpTo(AlphaFitnessDestination.Onboarding.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(AlphaFitnessDestination.ProfileEditor.route) {
            OnboardingRoute(
                mode = OnboardingMode.ProfileEdit,
                onCompleted = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AlphaFitnessDestination.MainShell.route) {
            AlphaFitnessMainShell(
                onOpenProfileEditor = {
                    navController.navigate(AlphaFitnessDestination.ProfileEditor.route)
                },
            )
        }
    }
}
