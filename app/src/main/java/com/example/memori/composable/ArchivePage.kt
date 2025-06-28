package com.example.memori.composable

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

/**
 * Composable function that displays the Archive Page.
 * This screen shows archived notes, allows navigation to other sections, and provides options
 * to manage folders, unarchive notes, or unlock protected folders.
 *
 * @param navController The NavController used for navigation between screens.
 * @param viewModel The ViewModel for managing notes, with a default factory for dependency injection.
 * @param folderViewModel The ViewModel for managing folders, with a default factory for dependency injection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivePage(
    navController: NavController,
    viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            context = LocalContext.current,
            repository = NotesRepository(
                NoteDatabase.getDatabase(LocalContext.current).noteDao()
            )
        )
    ),
    folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            context = LocalContext.current,
            repository = FolderRepository(
                NoteDatabase.getDatabase(context = LocalContext.current).folderDao()
            )
        )
    ),
) {
    // Context for accessing resources and preferences
    val context = LocalContext.current

    // State for archived notes
    val archivedNotes = viewModel.getArchivedNotes().collectAsStateWithLifecycle(initialValue = emptyList())
    val archivedNotesState = archivedNotes.value

    // State for folder creation dialog
    var folderName by remember { mutableStateOf("") }
    val foldersState by folderViewModel.allFolders.collectAsStateWithLifecycle()
    var showFolderDialog by remember { mutableStateOf(false) }

    // State for unarchive confirmation dialog
    var showUnarchiveDialog by remember { mutableStateOf(false) }

    // Navigation state
    val backStackEntry by navController.currentBackStackEntryAsState()
    var currentRoute = backStackEntry?.destination?.route

    // Drawer state for navigation drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Random kaomoji for empty archive message
    var randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    // Selection mode for managing multiple notes
    var selectionMode by remember { mutableStateOf(false) }
    val selectedNotes = remember { mutableStateListOf<NotesEntity>() }

    // State for PIN dialog
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    val pinHash by context.pinHashFlow().collectAsState(initial = null)
    var pinVisible by remember { mutableStateOf(false) }
    val pinValid = pinInput.length in 4..6

    // Modal navigation drawer for app navigation
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    // Drawer header
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memori",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation items
                    NavigationDrawerItem(
                        label = { Text(text = "Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home")
                            scope.launch { drawerState.close() }
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
                        label = { Text(text = "Archive") },
                        selected = currentRoute == "archive",
                        onClick = {
                            navController.navigate("archive")
                            scope.launch { drawerState.close() }
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

                    // Protected folder navigation
                    NavigationDrawerItem(
                        label = { Text(text = "Protected folder", color = MaterialTheme.colorScheme.onBackground) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (pinHash.isNullOrEmpty()) {
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

                    // Folder creation
                    NavigationDrawerItem(
                        label = { Text(text = "Create a new folder", color = MaterialTheme.colorScheme.onBackground) },
                        selected = false,
                        onClick = { showFolderDialog = true },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = "Create a new folder",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    // Display existing folders
                    if (foldersState.isNotEmpty()) {
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
                                label = { Text(text = folder.folderName, color = MaterialTheme.colorScheme.onBackground) },
                                selected = false,
                                onClick = {
                                    navController.navigate("folderNotes/${folder.id}/${folder.folderName}") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                    scope.launch { drawerState.close() }
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
        // Folder creation dialog
        if (showFolderDialog) {
            AlertDialog(
                onDismissRequest = { showFolderDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (folderName.isNotBlank()) {
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

        // PIN dialog for unlocking protected folder
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
                        val digest = MessageDigest.getInstance("SHA-256")
                        val hashBytes = digest.digest(pinInput.toByteArray(Charsets.UTF_8))
                        val attemptHash = hashBytes.joinToString("") { "%02x".format(it) }
                        if (attemptHash == pinHash) {
                            val prot = foldersState.find { it.folderName == "Protected" }
                            prot?.let {
                                navController.navigate("folderNotes/${it.id}/${it.folderName}") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                            showPinDialog = false
                            pinInput = ""
                        } else {
                            Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
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

        // Unarchive confirmation dialog
        if (showUnarchiveDialog) {
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
                icon = { Icon(Icons.Default.Unarchive, null) },
                dismissButton = {
                    TextButton(onClick = { showUnarchiveDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Unarchive notes") },
                text = { Text("Do you want to unarchive this notes") }
            )
        }

        // Main scaffold for the Archive Page
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
                        if (selectionMode) {
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
                                onClick = { showUnarchiveDialog = true }
                            ) {
                                Icon(Icons.Default.Unarchive, contentDescription = "Unarchive")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                // Display message if no archived notes are available
                if (archivedNotes.value.isEmpty()) {
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
                    // Display archived notes
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