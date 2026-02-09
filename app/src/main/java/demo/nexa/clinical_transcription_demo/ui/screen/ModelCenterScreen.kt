package demo.nexa.clinical_transcription_demo.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import demo.nexa.clinical_transcription_demo.R
import demo.nexa.clinical_transcription_demo.model.ModelInstallStatus
import demo.nexa.clinical_transcription_demo.presentation.ModelCenterViewModel
import demo.nexa.clinical_transcription_demo.ui.component.AuroraBackground
import demo.nexa.clinical_transcription_demo.ui.component.GlassCard
import demo.nexa.clinical_transcription_demo.ui.component.PrimaryGradientButton
import demo.nexa.clinical_transcription_demo.ui.component.SecondaryChip
import demo.nexa.clinical_transcription_demo.ui.theme.PlauColors
import demo.nexa.clinical_transcription_demo.ui.theme.PlauDimens
import demo.nexa.clinical_transcription_demo.nexa.NexaInitState
import demo.nexa.clinical_transcription_demo.nexa.NexaRuntime

@Composable
fun ModelCenterScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelCenterViewModel = viewModel()
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()
    val state by viewModel.state.collectAsState()
    val nexaState by NexaRuntime.state.collectAsState()

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
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PlauColors.IconGray
                        )
                    }
                    Column {
                        Text(
                            text = "Model Center",
                            fontSize = PlauDimens.textSizeTitle,
                            fontWeight = FontWeight.Medium,
                            color = PlauColors.TextPrimary
                        )
                        Text(
                            text = "Download once. Run fully on-device afterward.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlauColors.TextSecondary
                        )
                    }
                }

                // Avoid cramped headers on smaller devices.
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    SecondaryChip(
                        text = "Refresh",
                        enabled = !state.overallBusy
                    ) { viewModel.refresh() }
                }
            }

            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = PlauDimens.spacingMedium,
                    end = PlauDimens.spacingMedium,
                    top = PlauDimens.spacingSmall,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Quick Start",
                                style = MaterialTheme.typography.titleMedium,
                                color = PlauColors.TextPrimary
                            )
                            Text(
                                text = "Install ASR + Liquid to run the full Record → Transcribe → SOAP flow. Qwen is optional for higher-quality SOAP creation.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PlauColors.TextSecondary
                            )

                            when (val ns = nexaState) {
                                NexaInitState.Uninitialized -> {
                                    Text(
                                        text = "AI runtime: initializing…",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PlauColors.TextSecondary
                                    )
                                }
                                NexaInitState.Ready -> {
                                    Text(
                                        text = "AI runtime: ready",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PlauColors.TealDark
                                    )
                                }
                                is NexaInitState.Failed -> {
                                    val hint =
                                        if (ns.reason.contains("libOpenCL.so", ignoreCase = true) ||
                                            ns.reason.contains("cpu_gpu", ignoreCase = true)
                                        ) {
                                            "AI runtime failed (likely emulator/BrowserStack missing OpenCL). UI will still work; on-device inference needs a Snapdragon device."
                                        } else {
                                            "AI runtime failed: ${ns.reason}"
                                        }
                                    Text(
                                        text = hint,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PlauColors.AccentRed
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PrimaryGradientButton(
                                        text = "Install Required",
                                        iconRes = R.drawable.sparkles,
                                        enabled = !state.overallBusy
                                    ) { viewModel.installRequired() }
                                }
                                SecondaryChip(
                                    text = "Verify",
                                    enabled = !state.overallBusy
                                ) { viewModel.refresh() }
                            }
                        }
                    }
                }

                items(
                    count = state.rows.size,
                    key = { idx -> state.rows[idx].spec.id }
                ) { idx ->
                    val row = state.rows[idx]

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(animationSpec = tween(durationMillis = 180))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = row.spec.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PlauColors.TextPrimary
                                    )

                                    val statusText = when (row.status) {
                                        ModelInstallStatus.INSTALLED -> "Installed"
                                        ModelInstallStatus.PARTIAL -> "Partial"
                                        ModelInstallStatus.MISSING -> "Not installed"
                                    }
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PlauColors.TextSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    SecondaryChip(
                                        text = if (row.expanded) "Hide" else "Details",
                                        enabled = !row.isDownloading
                                    ) { viewModel.toggleExpanded(row.spec.id) }

                                    val primaryText = when {
                                        row.isDownloading -> "Working…"
                                        row.status == ModelInstallStatus.INSTALLED -> "Delete"
                                        else -> "Download"
                                    }
                                    SecondaryChip(
                                        text = primaryText,
                                        enabled = !row.isDownloading
                                    ) {
                                        if (row.status == ModelInstallStatus.INSTALLED) viewModel.delete(row.spec.id)
                                        else viewModel.download(row.spec.id)
                                    }
                                }
                            }

                            if (row.isDownloading) {
                                val pct = row.currentFilePct
                                if (pct != null) {
                                    LinearProgressIndicator(
                                        progress = { pct / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = PlauColors.TealPrimary,
                                        trackColor = PlauColors.TabBackground
                                    )
                                } else {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = PlauColors.TealPrimary,
                                        trackColor = PlauColors.TabBackground
                                    )
                                }
                            }

                            row.progressText?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PlauColors.TextSecondary
                                )
                            }

                            row.errorText?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PlauColors.AccentRed
                                )
                            }

                            AnimatedVisibility(visible = row.expanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Install path: ${row.spec.installRelPath}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PlauColors.TextSecondary
                                    )
                                    row.fileStatuses.forEach { fs ->
                                        val ok = fs.exists && fs.meetsMinBytes
                                        Text(
                                            text = "${if (ok) "✓" else "•"} ${fs.fileName}  (${fs.bytes / (1024 * 1024)}MB)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (ok) PlauColors.TealDark else PlauColors.TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
