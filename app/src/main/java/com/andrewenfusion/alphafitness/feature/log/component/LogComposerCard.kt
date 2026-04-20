package com.andrewenfusion.alphafitness.feature.log.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andrewenfusion.alphafitness.R
import com.andrewenfusion.alphafitness.core.designsystem.component.AlphaFitnessSectionCard
import com.andrewenfusion.alphafitness.core.designsystem.theme.AlphaFitnessSpacing
import androidx.compose.foundation.layout.Column

@Composable
fun LogComposerCard(
    draftMessage: String,
    canSubmit: Boolean,
    onDraftChanged: (String) -> Unit,
    onSubmitClicked: () -> Unit,
) {
    AlphaFitnessSectionCard(
        title = stringResource(id = R.string.log_composer_section_title),
        description = stringResource(id = R.string.log_composer_section_description),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AlphaFitnessSpacing.medium)) {
            OutlinedTextField(
                value = draftMessage,
                onValueChange = onDraftChanged,
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6,
                placeholder = {
                    Text(text = stringResource(id = R.string.log_composer_placeholder))
                },
                label = {
                    Text(text = stringResource(id = R.string.log_composer_label))
                },
            )

            Text(
                text = stringResource(id = R.string.log_composer_supporting_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = onSubmitClicked,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(id = R.string.log_composer_submit))
            }
        }
    }
}
