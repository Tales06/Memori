package com.example.memori.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function that displays an extended floating action button (FAB)
 * in the bottom right corner of the screen. When pressed, it navigates to the
 * note creation screen ("pageNotes") while preserving the state of the "home" screen.
 *
 * @param navController The NavController used for navigation between screens.
 */
@Composable
fun ButtonNote(navController: NavController){
    Box(modifier = Modifier.fillMaxSize()){

        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate("pageNotes") {
                    popUpTo("home") {
                        saveState = true
                    }
                }
            },
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Create Note", tint = MaterialTheme.colorScheme.onBackground) },
            text = { Text(text = "Create Note", color = MaterialTheme.colorScheme.onBackground) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    }
}