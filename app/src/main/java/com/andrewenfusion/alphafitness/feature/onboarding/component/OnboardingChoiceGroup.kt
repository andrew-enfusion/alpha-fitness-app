package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing

@Composable
fun <T> OnboardingChoiceGroup(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabelRes: (T) -> Int,
) {
    OnboardingSectionCard(title = title) {
        Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.xSmall)) {
            options.forEach { option ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = { onOptionSelected(option) },
                    )
                    Text(
                        text = stringResource(id = optionLabelRes(option)),
                        modifier = Modifier.padding(top = AlphaFitnessSpacing.small),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}
