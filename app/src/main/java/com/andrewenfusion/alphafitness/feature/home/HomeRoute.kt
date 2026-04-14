package com.andrewenfusion.alphafitness.feature.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(uiState = uiState.value)
}
