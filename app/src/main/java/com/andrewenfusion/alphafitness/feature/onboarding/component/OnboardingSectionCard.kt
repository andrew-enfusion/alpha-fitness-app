package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard

@Composable
fun OnboardingSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    AlphaFitnessSectionCard(
        title = title,
        modifier = modifier,
        description = description,
        content = content,
    )
}
