package com.example.memori.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memori.animation.AnimBackground
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@ExperimentalMaterial3ExpressiveApi
@Composable
fun DownloadContentFromCloudToDatabase(
    onDownloadComplete: () -> Unit = {}
) {
    // Animatable progress da 0f a 1f
    val progress = remember { Animatable(0f) }

    // Avvia l'animazione di caricamento
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 10000)
        )
        delay(300)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Sfondo animato
        AnimBackground()

        // Card centrale con leggera elevazione
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = "Download",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = "Downloading Content",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Indicator wave personalizzato con gradiente colorato
                LinearWavyProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                )
                            )
                        ),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(progress.value * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (progress.value >= 1f) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDownloadComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(text = "Let's Go!", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
