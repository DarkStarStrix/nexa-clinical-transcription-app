package demo.nexa.clinical_transcription_demo.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import demo.nexa.clinical_transcription_demo.model.ModelCatalog
import demo.nexa.clinical_transcription_demo.model.ModelDownloader
import demo.nexa.clinical_transcription_demo.model.ModelInstallStatus
import demo.nexa.clinical_transcription_demo.ui.component.AuroraBackground
import demo.nexa.clinical_transcription_demo.ui.component.GlassCard
import demo.nexa.clinical_transcription_demo.ui.component.PrimaryGradientButton
import demo.nexa.clinical_transcription_demo.ui.component.SecondaryChip
import demo.nexa.clinical_transcription_demo.ui.theme.PlauColors
import demo.nexa.clinical_transcription_demo.ui.theme.PlauDimens

@Composable
fun BountyBenchScreen(
    onBackClick: () -> Unit,
    onOpenModelCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()
    val context = LocalContext.current
    val downloader = remember { ModelDownloader(context.applicationContext) }

    val asrStatus = downloader.getInstallStatus(ModelCatalog.asrParakeetNexa)
    val liquidStatus = downloader.getInstallStatus(ModelCatalog.llmLiquidStarter)
    val qwenStatus = downloader.getInstallStatus(ModelCatalog.llmQwenUpgrade)

    val allRequiredOk = asrStatus == ModelInstallStatus.INSTALLED && liquidStatus == ModelInstallStatus.INSTALLED
    var showHelp by remember { mutableStateOf(true) }

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
                    Column {
                        Text(
                            text = "Bounty Bench",
                            fontSize = PlauDimens.textSizeTitle,
                            fontWeight = FontWeight.Medium,
                            color = PlauColors.TextPrimary
                        )
                        Text(
                            text = "Hexagon NPU demo checklist and status.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlauColors.TextSecondary
                        )
                    }
                }

                SecondaryChip(text = "Models") { onOpenModelCenter() }
            }

            val contentPadding = PaddingValues(
                start = PlauDimens.spacingMedium,
                end = PlauDimens.spacingMedium,
                top = PlauDimens.spacingSmall,
                bottom = 24.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Hexagon NPU Demo",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlauColors.TextPrimary
                        )
                        Text(
                            text = "LLM backend tries HTP0 first, then GPUOpenCL, then CPU. ASR uses Nexa NPU plugin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PlauColors.TextSecondary
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SecondaryChip(text = "ASR: ${asrStatus.name.lowercase().replaceFirstChar { it.uppercase() }}") {}
                            SecondaryChip(text = "Liquid: ${liquidStatus.name.lowercase().replaceFirstChar { it.uppercase() }}") {}
                            SecondaryChip(text = "Qwen: ${qwenStatus.name.lowercase().replaceFirstChar { it.uppercase() }}") {}
                        }
                        PrimaryGradientButton(
                            text = if (allRequiredOk) "Ready to demo" else "Install Required Models",
                            enabled = true,
                            onClick = onOpenModelCenter
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showHelp,
                    enter = androidx.compose.animation.fadeIn(tween(180)) + androidx.compose.animation.slideInVertically(tween(180)) { it / 6 }
                ) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "What to show judges",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PlauColors.TextPrimary
                                )
                                Text(
                                    text = "Hide",
                                    color = PlauColors.TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable(
                                        onClick = { showHelp = false },
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                                )
                            }

                            Text(
                                text = "1) Model Center: ASR + Liquid installed.\n2) Record a note.\n3) Note: Transcribe Audio.\n4) Summary: Backend shows HTP0 (or fallback shown).",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PlauColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
