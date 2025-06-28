package com.example.memori.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyInfo(
    modifier: Modifier = Modifier,
    onAcceptedChange: (Boolean) -> Unit = {},
    onContinueClicked: () -> Unit = {}
) {
    var isAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔐 Privacy Notice",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = """
                Welcome to Memori!

                We value your privacy. Here's what we collect:
                • Your name and email (if you log in with Google)
                • Your notes, folders, and media
                • Basic connection info (used only to sync with your cloud)

                Where your data goes:
                • Saved locally on your device
                • Optionally synced securely with Google Firebase

                No ads. No data selling. Ever.

                You can disable sync, delete your account or notes anytime.

                By checking below, you agree to our Privacy Policy.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isAccepted,
                onCheckedChange = {
                    isAccepted = it
                    onAcceptedChange(it)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "I accept the Privacy Policy")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinueClicked,
            enabled = isAccepted
        ) {
            Text("Continue")
        }
    }
}
