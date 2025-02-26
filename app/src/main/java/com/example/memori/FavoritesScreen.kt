package com.example.memori

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun FavoritesScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(NoteDatabase.getDatabase(
                context = LocalContext.current

            ).noteDao())
        ))
){
    val noteState by noteViewModel.getFavoritesNote().collectAsState(initial = emptyList())

    Surface (
        color = MaterialTheme.colorScheme.background,
    ){
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            if(noteState.isEmpty()){
                Text(
                    text = "No favorites yet",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)

                )
            }else{
                NoteCard(noteState, navController)
            }
        }
    }
}