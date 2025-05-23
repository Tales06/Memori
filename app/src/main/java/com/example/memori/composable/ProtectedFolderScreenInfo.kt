package com.example.memori.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ProtectedFolderInfoScreen(
    onCancel: () -> Unit,
    onProceed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Protected Folder Icon",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Secure Your Notes",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Protected folders allow you to store sensitive notes with an extra layer of security.\n\n" +
                        "You’ll need to authenticate via PIN each time you want to access them.\n\n" +
                        "Ideal for storing personal thoughts, credentials, or confidential information." +
                        "Remember you can only create only one protected folder",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onProceed
                ) {
                    Text("Set Up Now")
                }
            }
        }
    }
}
