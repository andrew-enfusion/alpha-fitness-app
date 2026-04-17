package com.andrewenfusion.alphafitness.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessNavGlyph
import com.andrewenfusion.alphafitness.feature.calendar.CalendarRoute
import com.andrewenfusion.alphafitness.feature.calendar.daydetail.CalendarDayDetailRoute
import com.andrewenfusion.alphafitness.feature.insights.InsightsRoute
import com.andrewenfusion.alphafitness.feature.log.LogRoute
import com.andrewenfusion.alphafitness.feature.profile.ProfileRoute

@Composable
fun AlphaFitnessMainShell(
    onOpenProfileEditor: () -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                AlphaFitnessTopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination.isTopLevelDestination(destination)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            AlphaFitnessNavGlyph(
                                glyph = destination.glyph,
                                selected = selected,
                            )
                        },
                        label = {
                            androidx.compose.material3.Text(
                                text = stringResource(id = destination.labelRes),
                            )
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AlphaFitnessDestination.Log.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(AlphaFitnessDestination.Log.route) {
                LogRoute()
            }
            composable(AlphaFitnessDestination.Calendar.route) {
                CalendarRoute(
                    onOpenDayDetail = { dateLabel ->
                        navController.navigate(
                            AlphaFitnessDestination.CalendarDayDetail.createRoute(dateLabel),
                        )
                    },
                )
            }
            composable(
                route = AlphaFitnessDestination.CalendarDayDetail.route,
                arguments = listOf(
                    navArgument(AlphaFitnessDestination.CalendarDayDetail.dateLabelArg) {
                        type = NavType.StringType
                    },
                ),
            ) { backStackEntry ->
                val dateLabel = backStackEntry.arguments
                    ?.getString(AlphaFitnessDestination.CalendarDayDetail.dateLabelArg)
                    .orEmpty()
                CalendarDayDetailRoute(
                    dateLabel = dateLabel,
                    onNavigateBack = { navController.popBackStack() },
                )
            }
            composable(AlphaFitnessDestination.Insights.route) {
                InsightsRoute()
            }
            composable(AlphaFitnessDestination.Profile.route) {
                ProfileRoute(
                    onEditProfile = onOpenProfileEditor,
                )
            }
        }
    }
}

private fun NavDestination?.isTopLevelDestination(
    destination: AlphaFitnessTopLevelDestination,
): Boolean {
    val route = this?.route ?: return false

    return when (destination) {
        AlphaFitnessTopLevelDestination.Log -> hierarchy.any { it.route == AlphaFitnessDestination.Log.route }
        AlphaFitnessTopLevelDestination.Calendar -> {
            route == AlphaFitnessDestination.Calendar.route ||
                route == AlphaFitnessDestination.CalendarDayDetail.route
        }
        AlphaFitnessTopLevelDestination.Insights -> hierarchy.any { it.route == AlphaFitnessDestination.Insights.route }
        AlphaFitnessTopLevelDestination.Profile -> hierarchy.any { it.route == AlphaFitnessDestination.Profile.route }
    }
}
