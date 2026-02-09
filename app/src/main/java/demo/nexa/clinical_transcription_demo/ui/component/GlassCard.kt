package demo.nexa.clinical_transcription_demo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import demo.nexa.clinical_transcription_demo.ui.theme.PlauColors
import demo.nexa.clinical_transcription_demo.ui.theme.PlauDimens

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    corner: Dp = 22.dp,
    padding: Dp = PlauDimens.spacingMedium,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Card(
        modifier = modifier
            .border(1.dp, Color.White.copy(alpha = 0.55f), shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = PlauColors.SurfaceWhite.copy(alpha = 0.86f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.02f))
                .padding(padding)
        ) {
            content()
        }
    }
}

