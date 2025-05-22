package com.example.memori.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun AnimBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val scale1 by infiniteTransition.animateFloat(
        1f,
        1.2f,
        infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse)
    )
    val scale2 by infiniteTransition.animateFloat(
        1f,
        1.4f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse)
    )
    val scale3 by infiniteTransition.animateFloat(
        1f,
        1.6f,
        infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse)
    )
    val scale4 by infiniteTransition.animateFloat(
        1f,
        1.8f,
        infiniteRepeatable(tween(5500, easing = LinearEasing), RepeatMode.Reverse)
    )
    val scale5 by infiniteTransition.animateFloat(
        1f,
        2.0f,
        infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        // Bolla in alto a sinistra
        Box(
            modifier = Modifier
                .scale(scale1)
                .size(180.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .background(
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    RoundedCornerShape(90.dp)
                )
        )

        // Bolla in basso a destra
        Box(
            modifier = Modifier
                .scale(scale2)
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 60.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    RoundedCornerShape(80.dp)
                )
        )

        // Bolla centrale in alto
        Box(
            modifier = Modifier
                .scale(scale3)
                .size(120.dp)
                .align(Alignment.TopCenter)
                .offset(y = 40.dp)
                .background(
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                    RoundedCornerShape(60.dp)
                )
        )

        // Bolla centrale in basso
        Box(
            modifier = Modifier
                .scale(scale4)
                .size(100.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    RoundedCornerShape(50.dp)
                )
        )

        // Bolla centrale grande al centro
        Box(
            modifier = Modifier
                .scale(scale5)
                .size(220.dp)
                .align(Alignment.Center)
                .offset(x = 30.dp, y = (-50).dp)
                .background(
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f),
                    RoundedCornerShape(110.dp)
                )
        )
    }
}
