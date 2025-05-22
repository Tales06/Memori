package com.example.memori.composable


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesEntity
import com.example.memori.database.note_data.NotesRepository

@Composable
fun FavoritesScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(
                context = LocalContext.current

            ).noteDao())
        )
    )
){
    val noteState by noteViewModel.getFavoritesNote().collectAsState(initial = emptyList())

    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedNotes = remember { mutableStateListOf<NotesEntity>() }

    Surface (
        color = MaterialTheme.colorScheme.background,
    ){
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            if(noteState.isEmpty()){
                Text(
                    text = randomKaomoji,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)

                )


                Text(
                    text = "No favorites notes yet",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center).padding(top = 90.dp)
                )
            }else{
                NoteCard(
                    notes = noteState,
                    navController = navController,
                    onNoteLongPress = { note ->
                        selectionMode = true
                        selectedNotes.add(note)
                    },
                    onNoteClick = { note ->
                        if (selectionMode) {
                            if (selectedNotes.contains(note)) {
                                selectedNotes.remove(note)
                                if (selectedNotes.isEmpty()) selectionMode = false
                            } else {
                                selectedNotes.add(note)
                            }
                        } else {
                            navController.navigate("modifiedNotes/${note.id}")
                        }
                    }
                )
            }
        }
    }
}