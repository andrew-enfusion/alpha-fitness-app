package com.andrewenfusion.alphafitness

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessTheme
import com.andrewenfusion.alphafitness.navigation.AlphaFitnessNavHost

@Composable
fun AlphaFitnessApp() {
    AlphaFitnessTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AlphaFitnessNavHost(
                navController = rememberNavController(),
            )
        }
    }
}
