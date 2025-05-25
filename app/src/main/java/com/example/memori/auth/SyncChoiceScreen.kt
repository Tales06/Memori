package com.example.memori.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memori.animation.AnimBackground

/**
 * Composable function that displays the Sync Choice screen.
 * This screen allows the user to choose whether to sync their notes or skip the sync process.
 *
 * @param onChooseSync Callback invoked when the user chooses to sync their notes.
 * @param onSkipSync Callback invoked when the user decides to skip syncing their notes.
 * @param onBack Callback invoked when the user chooses to go back to the previous screen.
 */
@Composable
fun SyncChoiceScreen(
    onChooseSync: () -> Unit,
    onSkipSync: () -> Unit,
    onBack: () -> Unit
) {
    // Main container for the screen
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background
        AnimBackground()

        // Centered content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title text
            Text(
                text = "Do you want to sync your notes?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description text
            Text(
                text = "In this way, you will be able to share your notes with other users.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Button to choose sync
            Button(
                onClick = onChooseSync,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text("Yes, sync my notes", color = MaterialTheme.colorScheme.onPrimary)
            }

            // Button to skip sync
            OutlinedButton(
                onClick = onSkipSync,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("No, skip sync", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informational text
            Text(
                text = "You can always change this option in the settings.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to go back
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text("Go back to change the theme", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}