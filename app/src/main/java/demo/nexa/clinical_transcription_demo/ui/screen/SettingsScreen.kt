package demo.nexa.clinical_transcription_demo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import demo.nexa.clinical_transcription_demo.presentation.SettingsViewModel
import demo.nexa.clinical_transcription_demo.ui.component.AuroraBackground
import demo.nexa.clinical_transcription_demo.ui.component.GlassCard
import demo.nexa.clinical_transcription_demo.ui.component.SecondaryChip
import demo.nexa.clinical_transcription_demo.ui.theme.PlauColors
import demo.nexa.clinical_transcription_demo.ui.theme.PlauDimens

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showClearModelsDialog by remember { mutableStateOf(false) }

    AuroraBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = PlauDimens.spacingMedium,
                        top = statusBarsPadding.calculateTopPadding() + PlauDimens.spacingSmall,
                        end = PlauDimens.spacingMedium,
                        bottom = PlauDimens.spacingSmall
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PlauColors.IconGray
                        )
                    }
                    Text(
                        text = "Settings",
                        fontSize = PlauDimens.textSizeTitle,
                        fontWeight = FontWeight.Medium,
                        color = PlauColors.TextPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PlauDimens.spacingMedium),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Privacy",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlauColors.TextPrimary
                        )
                        Text(
                            text = "Recordings and notes are stored locally on this device. You can clear all local data at any time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PlauColors.TextSecondary
                        )
                        SecondaryChip(text = "Clear all local notes") { showClearAllDialog = true }
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Models",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlauColors.TextPrimary
                        )
                        Text(
                            text = "Downloaded models can take significant storage.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PlauColors.TextSecondary
                        )
                        SecondaryChip(text = "Clear downloaded models") { showClearModelsDialog = true }
                    }
                }
            }
        }
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear all notes?") },
            text = { Text("This deletes all recordings and note data from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearAllDialog = false
                    viewModel.clearAllNotes()
                }) {
                    Text("Delete", color = PlauColors.AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearModelsDialog) {
        AlertDialog(
            onDismissRequest = { showClearModelsDialog = false },
            title = { Text("Clear downloaded models?") },
            text = { Text("This deletes downloaded model files. You can re-download later.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearModelsDialog = false
                    viewModel.clearModels()
                }) {
                    Text("Delete", color = PlauColors.AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearModelsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

