package demo.nexa.clinical_transcription_demo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import demo.nexa.clinical_transcription_demo.ui.theme.PlauColors
import demo.nexa.clinical_transcription_demo.ui.theme.PlauGradients

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val c = PlauGradients.primaryColors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlauColors.BackgroundAqua)
            .background(
                Brush.radialGradient(
                    colors = listOf(c[0].copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(0.15f, 0.12f),
                    radius = 1200f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(c[1].copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(0.85f, 0.18f),
                    radius = 1200f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(c[2].copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(0.70f, 0.95f),
                    radius = 1400f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(c[3].copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(0.10f, 0.80f),
                    radius = 1400f
                )
            )
    ) {
        content()
    }
}

