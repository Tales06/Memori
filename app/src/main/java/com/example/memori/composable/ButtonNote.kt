package com.example.memori.composable



import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.memori.theme.MyPalette

@Composable
fun ButtonNote(navController: NavController){
    Box(modifier = Modifier.fillMaxSize(),){

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













