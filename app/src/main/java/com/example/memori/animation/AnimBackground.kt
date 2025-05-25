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

/**
 * Composable that creates an animated background with floating bubbles.
 * Each bubble is animated with a scaling effect using infinite transitions.
 *
 * The animations are managed using Jetpack Compose coroutines and
 * rememberInfiniteTransition, which allows values to be animated in a loop.
 *
 * @see androidx.compose.runtime.Composable
 * @see androidx.compose.animation.core.rememberInfiniteTransition
 */
@Composable
fun AnimBackground() {
    // Infinite transition to animate the scale of the bubbles
    val infiniteTransition = rememberInfiniteTransition()

    // Scale animation for the first bubble
    val scale1 by infiniteTransition.animateFloat(
        1f,
        1.2f,
        infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse)
    )
    // Scale animation for the second bubble
    val scale2 by infiniteTransition.animateFloat(
        1f,
        1.4f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse)
    )
    // Scale animation for the third bubble
    val scale3 by infiniteTransition.animateFloat(
        1f,
        1.6f,
        infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse)
    )
    // Scale animation for the fourth bubble
    val scale4 by infiniteTransition.animateFloat(
        1f,
        1.8f,
        infiniteRepeatable(tween(5500, easing = LinearEasing), RepeatMode.Reverse)
    )
    // Scale animation for the fifth bubble
    val scale5 by infiniteTransition.animateFloat(
        1f,
        2.0f,
        infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse)
    )

    // Main container with a vertical gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        // Top-left bubble
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

        // Bottom-right bubble
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

        // Top-center bubble
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

        // Bottom-center bubble
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

        // Large central bubble
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