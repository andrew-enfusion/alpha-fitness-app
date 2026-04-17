package com.andrewenfusion.alphafitness.feature.appentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessHeroCard
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessScreenScaffold
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing

@Composable
fun AppEntryRoute(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMainShell: () -> Unit,
    viewModel: AppEntryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            AppEntryDestination.ONBOARDING -> onNavigateToOnboarding()
            AppEntryDestination.MAIN_SHELL -> onNavigateToMainShell()
            null -> Unit
        }
    }

    AlphaFitnessScreenScaffold(
        title = stringResource(id = R.string.app_entry_title),
        subtitle = stringResource(id = R.string.app_entry_subtitle),
    ) {
        item {
            AlphaFitnessHeroCard(
                label = stringResource(id = R.string.app_entry_label),
                title = stringResource(id = R.string.app_entry_heading),
                body = stringResource(id = R.string.app_entry_body),
                supportingText = stringResource(id = R.string.app_entry_supporting),
            )
        }

        item {
            Column(
                modifier = Modifier
                    .padding(AlphaFitnessSpacing.large),
                verticalArrangement = Arrangement.Center,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
