package com.example.memori.composable

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesEntity
import com.example.memori.database.note_data.NotesRepository
import com.example.memori.preference.PinPreferences.pinHashFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivePage(
    navController: NavController,
    viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(LocalContext.current).noteDao()
            )
        )
    ),
    folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            repository = FolderRepository(
                NoteDatabase.getDatabase(context = LocalContext.current).folderDao()
            )
        )
    ),
) {
    val context = LocalContext.current

    val archivedNotes = viewModel.getArchivedNotes().collectAsStateWithLifecycle(initialValue = emptyList())
    val archivedNotesState = archivedNotes.value

    var folderName by remember { mutableStateOf("") }
    val foldersState by folderViewModel.allFolders.collectAsStateWithLifecycle()
    var showFolderDialog by remember { mutableStateOf(false) }

    var showUnarchiveDialog by remember { mutableStateOf(false) }


    val backStackEntry by navController.currentBackStackEntryAsState()
    var currentRoute = backStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    var selectionMode by remember { mutableStateOf(false) }
    val selectedNotes = remember { mutableStateListOf<NotesEntity>() }

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    val pinHash by context.pinHashFlow().collectAsState(initial = null)
    var pinVisible by remember { mutableStateOf(false) }
    val pinValid = pinInput.length in 4..6

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memori",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Home",
                            )
                        },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home")
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home",
                                tint = if (currentRoute == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Archive",
                            )
                        },
                        selected = currentRoute == "archive",
                        onClick = {
                            navController.navigate("archive")
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "archive") Icons.Filled.Archive else Icons.Outlined.Archive,
                                contentDescription = "Archive",
                                tint = if (currentRoute == "archive") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Protected folder",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (pinHash == null || pinHash == "") {
                                    navController.navigate("protected_info")
                                } else {
                                    showPinDialog = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Protected folder",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)

                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Create a new folder",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = false,
                        onClick = {
                            // Handle folder creation
                            showFolderDialog = true
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = "Create a new folder",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    if(foldersState.isNotEmpty()){
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Folders",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(16.dp)
                        )
                        foldersState.forEach { folder ->
                            if (folder.folderName == "Protected") return@forEach
                            Spacer(modifier = Modifier.height(8.dp))
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = folder.folderName,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                selected = false,
                                onClick = {
                                    // Handle folder selection
                                    navController.navigate("folderNotes/${folder.id}/${folder.folderName}") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Folder,
                                        contentDescription = "Folder",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            }
        }
    ) {
        if(showFolderDialog) {
            AlertDialog(
                onDismissRequest = { showFolderDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (folderName.isNotBlank()) {
                            // Salva nel database
                            folderViewModel.createFolder(folderName.trim(), context)
                        }
                        folderName = ""
                        showFolderDialog = false
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        folderName = ""
                        showFolderDialog = false
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("New folder") },
                text = {
                    OutlinedTextField(
                        value = folderName,
                        onValueChange = { folderName = it },
                        label = { Text("Name of the folder") }
                    )
                }
            )
        }
        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text("Unlock protected folder") },
                text = {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { pinVisible = !pinVisible }) {
                                Icon(
                                    imageVector = if (pinVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (pinVisible) "Hide PIN" else "Show PIN"
                                )
                            }
                        },
                        isError = pinInput.isNotEmpty() && !pinValid,

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
                                navController.navigate("folderNotes/${it.id}/${it.folderName}") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                            showPinDialog = false
                            pinInput = ""
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
                },

            )
        }
        if(showUnarchiveDialog) {
            AlertDialog(
                onDismissRequest = {
                    showUnarchiveDialog = false
                    selectedNotes.clear()
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedNotes.forEach { note ->
                                viewModel.unArchiveNote(note.id)
                            }
                            showUnarchiveDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                icon = {
                    Icon(Icons.Default.Unarchive, null)
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showUnarchiveDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text("Unarchive notes")
                },
                text = {
                    Text("Do you want to unarchive this notes")
                }
            )
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Archive",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            },
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if(selectionMode) {
                            IconButton(
                                onClick = {
                                    selectedNotes.forEach { note ->
                                        viewModel.delete(note.id)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                            IconButton(
                                onClick = {
                                    showUnarchiveDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Unarchive, contentDescription = "Unarchive")
                            }
                        }
                    }
                )
            }
        ) {
            innerPadding ->
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(innerPadding)) {
                if(archivedNotes.value.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = randomKaomoji,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.Center)

                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "No notes in the archive yet",
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
                        notes = archivedNotesState,
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
                        },
                        selectedNotes = selectedNotes,
                    )
                }
            }
        }
    }
}