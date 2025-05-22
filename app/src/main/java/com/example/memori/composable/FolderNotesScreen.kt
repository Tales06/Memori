package com.example.memori.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesEntity
import com.example.memori.database.note_data.NotesRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderNotesScreen(
    folderId: Int,
    folderName: String,
    navController: NavHostController,
    viewModelNote: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(context = LocalContext.current).noteDao()
            )
        )
    ),
    viewModelFolder: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            repository = FolderRepository(
                NoteDatabase.getDatabase(context = LocalContext.current).folderDao()
            )
        )
    ),
) {
    val notesInFolder by viewModelNote.getNotesInFolder(folderId).collectAsStateWithLifecycle(
        emptyList()
    )

    val folder by viewModelFolder.getFolderByUuid(folderId.toString()).collectAsStateWithLifecycle(
        initialValue = null
    )

    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedNotes = remember { mutableStateListOf<NotesEntity>() }

    var showDialogForDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = folderName) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("folderNotes/$folderId/$folderName") {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showDialogForDelete = true
                        }
                    ){
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize(),){

                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("pageNotes/$folderId/$folderName") {
                            popUpTo("folderNotes/$folderId/$folderName") {
                                inclusive = true
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
    ) { innerPadding ->

        if(showDialogForDelete) {
            GenericAlertDialog(
                onDismissRequest = { showDialogForDelete = false },
                onConfirmation = {

                    notesInFolder.forEach { note ->
                        viewModelNote.delete(note.id)
                    }
                    viewModelFolder.deleteFolder(folderId)

                    showDialogForDelete = false

                    navController.navigate("home") {
                        popUpTo("folderNotes/$folderId/$folderName") {
                            inclusive = true
                        }
                    }

                },
                dialogTitle = "Delete Notes",
                dialogText = "Are you sure you want to delete these notes into the folder? The folder will be deleted too. This action cannot be undone.",
                icon = Icons.Filled.Delete,
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {



                Box(modifier = Modifier.fillMaxSize()) {
                    if (notesInFolder.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = randomKaomoji,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = TextStyle(
                                    fontSize = 45.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.align(Alignment.Center)

                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "No notes in this folder yet",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 90.dp)
                            )
                        }
                    } else {
                        NoteCard(
                            notes = notesInFolder,
                            navController = navController,
                            selectedNotes = selectedNotes,
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
                            },
                        )
                    }
                }
            }


        }
    }
}