package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessHeroCard

@Composable
fun OnboardingAssistantCard(
    label: String,
    headline: String,
    message: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    AlphaFitnessHeroCard(
        label = label,
        title = headline,
        body = message,
        modifier = modifier,
        supportingText = supportingText,
    )
}
