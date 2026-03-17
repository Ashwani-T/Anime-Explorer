package com.example.animeexplorer.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ArcLoader(
    size: Dp = 80.dp,
    strokeWidth: Dp = 6.dp,
    color: Color = Color(0xFF6200EE)
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = ""
    )

    Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {

        drawArc(
            brush = Brush.sweepGradient(
                listOf(Color.Magenta, Color.Cyan, Color.Magenta)
            ),
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}



@Composable
fun LinearProgressCustom(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    cornerRadius: Dp = 50.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    indicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    val p = progress.coerceIn(0f, 1f)
    Box(
        modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(trackColor)
    ) {
        Box(
            Modifier
                .fillMaxWidth(p)
                .fillMaxHeight()
                .background(indicatorColor)
        )
    }
}