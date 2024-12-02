package com.example.memori

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(){
    Surface (
        color = MaterialTheme.colorScheme.onBackground,

    ) {
        Box(
            contentAlignment = Alignment.Center
        ){
            Text(text = "Settings Screen", color = MaterialTheme.colorScheme.primary)

        }
    }
}