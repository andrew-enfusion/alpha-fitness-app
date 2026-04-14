package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing

@Composable
fun OnboardingStatusCard(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(AlphaFitnessSpacing.medium),
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
