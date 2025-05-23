package com.example.memori.composable

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DriveFileMove
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.example.memori.preference.PinPreferences
import com.example.memori.preference.PinPreferences.deletePinHash
import com.example.memori.preference.PinPreferences.pinHashFlow
import com.example.memori.preference.PinPreferences.savePinHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest

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
    val context = LocalContext.current

    val notesInFolder by viewModelNote.getNotesInFolder(folderId).collectAsStateWithLifecycle(
        emptyList()
    )

    // dentro FolderNotesScreen, subito prima di `if(showDialogForMoveToAnotherFolder)`


    val foldersState by viewModelFolder.allFolders.collectAsStateWithLifecycle()


    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    var selectionMode by remember { mutableStateOf(false) }
    val selectedNotes = remember { mutableStateListOf<NotesEntity>() }

    var showDialogForDelete by remember { mutableStateOf(false) }
    var showDialogForMoveToAnotherFolder by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    val pinHash by context.pinHashFlow().collectAsState(initial = null)

    val scope = rememberCoroutineScope()

    var destFolderId by remember { mutableStateOf<Int?>(folderId) }
    LaunchedEffect(showDialogForMoveToAnotherFolder) {
        if (showDialogForMoveToAnotherFolder) {
            // all’apertura del dialog pre‐seleziono la cartella corrente
            destFolderId = folderId
        }
    }



    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text(text = "${selectedNotes.size} notes selected") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                selectionMode = false
                                selectedNotes.clear()
                            }
                        ) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                selectedNotes.forEach { note ->
                                    viewModelNote.delete(note.id)
                                }
                                selectionMode = false
                                selectedNotes.clear()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(
                            onClick = {
                                showDialogForMoveToAnotherFolder = true
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.DriveFileMove,
                                contentDescription = "Move to folder"
                            )
                        }
                    }
                )
            } else {

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
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                    }
                )
            }
        },
        floatingActionButton = {
            if (folderName == "Protected") {
                return@Scaffold
            }
            Box(modifier = Modifier.fillMaxSize()) {

                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("pageNotes/$folderId/$folderName") {
                            popUpTo("folderNotes/$folderId/$folderName") {
                                inclusive = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Create Note",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    text = {
                        Text(
                            text = "Create Note",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            }
        }
    ) { innerPadding ->

        if (showDialogForDelete) {
            GenericAlertDialog(
                onDismissRequest = { showDialogForDelete = false },
                onConfirmation = {
                    if (folderName == "Protected") {
                        showDialogForDelete = false
                        showPinDialog = true
                        return@GenericAlertDialog
                    }
                    scope.launch {
                        notesInFolder.forEach { note ->
                            viewModelNote.delete(note.id)
                        }
                        val prot = foldersState.find { it.folderName == folderName }
                        prot?.let {
                            viewModelFolder.deleteFolder(it.id, it.folderUuid)
                        }
                    }

                    showDialogForDelete = false

                    navController.navigate("home") {
                        popUpTo("folderNotes/$folderId/$folderName") {
                            inclusive = true
                        }
                    }

                },
                dialogTitle = "Delete Notes",
                dialogText = if (folderName == "Protected") {
                    "Are you sure you want to delete this protected folder? This action cannot be undone."
                } else {
                    "Are you sure you want to delete all notes in this folder? This action cannot be undone."
                },
                icon = Icons.Filled.Delete,
            )
        }

        if (showDialogForMoveToAnotherFolder) {
            AlertDialog(
                title = { Text("Move to another folder") },
                onDismissRequest = {
                    showDialogForMoveToAnotherFolder = false
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialogForMoveToAnotherFolder = false
                            selectionMode = false
                            selectedNotes.clear()
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (destFolderId == null) {

                                selectedNotes.forEach { note ->
                                    viewModelNote.clearNoteFolder(note.id)
                                }
                            } else {

                                selectedNotes.forEach { note ->
                                    viewModelNote.moveNoteToFolder(note.id, destFolderId!!)
                                }

                            }
                            showDialogForMoveToAnotherFolder = false
                            selectionMode = false
                            selectedNotes.clear()
                        }
                    ) {
                        Text("Move")
                    }
                },
                text = {
                    Column {
                        Card(
                            onClick = { destFolderId = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = if (destFolderId == null)
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(Icons.Outlined.FolderOff, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("No folder", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        foldersState.forEach { folder ->
                            val isSelected = destFolderId == folder.id
                            Card(
                                onClick = {
                                    destFolderId = folder.id
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (isSelected) 8.dp else 0.dp
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary
                                ) else null
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Folder,
                                        contentDescription = "Folder icon",
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = folder.folderName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text("Unlock to delete protected folder") },
                text = {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Calcola hash di pinInput
                        val digest = MessageDigest.getInstance("SHA-256")
                        val hashBytes = digest.digest(pinInput.toByteArray(Charsets.UTF_8))
                        val attemptHash = hashBytes.joinToString("") { "%02x".format(it) }
                        if (attemptHash == pinHash) {
                            // Trova cartella e naviga
                            val prot = foldersState.find { it.folderName == "Protected" }
                            prot?.let {
                                scope.launch {

                                    notesInFolder.forEach { note ->
                                        viewModelNote.delete(note.id)
                                    }
                                    viewModelFolder.deleteFolder(it.id, it.folderUuid)

                                    context.deletePinHash()
                                    navController.navigate("home") {
                                        popUpTo("folderNotes/$folderId/$folderName") {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                            pinInput = ""
                            showPinDialog = false
                        } else {
                            Toast.makeText(
                                context,
                                "Incorrect PIN",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text("Cancel")
                    }
                }
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
                                    navController.navigate("modifiedNotes/${note.id}/${folderId}/${folderName}")
                                }
                            },
                        )
                    }
                }
            }


        }
    }
}