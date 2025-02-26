package com.example.memori



import android.content.pm.ModuleInfo
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ButtonNote(navController: NavController){
    Box(modifier = Modifier.fillMaxSize(),){

        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate("pageNotes")
            },
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Create Note") },
            text = { Text(text = "Create Note") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}













