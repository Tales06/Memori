package com.example.memori.composable

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

import kotlinx.coroutines.launch

val kaomoji: List<String> = listOf(
    "╥﹏╥)",
    "(ಥ﹏ಥ)",
    "(ಥ_ಥ)",
    "(´；ω；｀)",
    "(｡•́︿•̀｡)",
    "(ಥ‿ಥ)",
    "(╯︵╰,)",
    "(ﾉД`)",
    "(つ﹏⊂)",
    "(இ﹏இ)",
    "(｀Д´)",
    "(¬_¬)",
    "(눈_눈)",
    "(ノಠ益ಠ)ノ",
    "(ง'̀-'́)ง",
    "(¬▂¬)",
    "(ಠ_ಠ)",
    "(ಠ益ಠ)",
    "(⊙_⊙)",
    "(ﾟoﾟ)",
    "(o_O)",
    "(°ロ°)",
    "(o.o)",
    "(O.O)",
    "(ʘᗩʘ’)",
    "(￣□￣)",
    "Σ(°△°|||)",
    "(⊙_⊙;)",
    "(－_－) zzZ",
    "(￣o￣) zzZ",
    "(∪｡∪)｡｡｡zzz",
    "(︶︹︺)",
    "(っ˘ω˘ς )",
    "(－ω－) zzZ",
    "(´〜｀*) zzz",
    "(≚ᄌ≚)ƶƶ",
    "(∪｡∪) zzZ",
    "(￣ρ￣)..zzZZ"
)

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(context = LocalContext.current).noteDao()
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

    searchExpanded: Boolean,
    onSearchExpanded: (Boolean) -> Unit
) {
    val notesState by noteViewModel.allNotes.collectAsStateWithLifecycle(initialValue = emptyList())
    val foldersState by folderViewModel.allFolders.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var isSearchBarVisible by remember { mutableStateOf(true) }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedNotes = remember { mutableStateListOf<NotesEntity>() }


    // Osserva il comportamento dello scroll
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        isSearchBarVisible =
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 10

        Log.e("cartelle", "cartelle: $foldersState")
    }



    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    var showFolderDialog by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val context = LocalContext.current


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
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
                            navController.navigate("home") {
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
                            navController.navigate("archive") {
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
                        LazyColumn(

                        ) {
                            items(foldersState) { folder ->
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
        }
    ) {

        if(showFolderDialog) {
            AlertDialog(
                onDismissRequest = { showFolderDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        //Controllo se esiste già una cartella con lo stesso nome
                        val folderExist = foldersState.any { it.folderName == folderName.trim() }
                        if (folderExist) {
                            Toast.makeText(
                                context,
                                "Folder already exists",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        } else if(folderName.contains(" ")) {
                            Toast.makeText(
                                context,
                                "Please insert a name for the folder without spaces",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton


                        }




                        if (folderName.isNotBlank()) {
                            // Salva nel database
                            folderViewModel.createFolder(folderName.trim())
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

        Scaffold(
            contentWindowInsets = WindowInsets.ime,
            topBar = {
                if (selectionMode) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Select notes ${selectedNotes.size}",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    selectedNotes.clear()
                                    selectionMode = false
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        actions = {
                            IconButton(
                                onClick = {
                                    selectedNotes.forEach { note ->
                                        noteViewModel.delete(note.id)
                                    }
                                    selectedNotes.clear()
                                    selectionMode = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                    )
                } else {
                    SearchBarComponent(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        modifier = Modifier.fillMaxWidth(),
                        expanded = searchExpanded,
                        onExpandedChange = onSearchExpanded,
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
                        onNoteLongPress = { note ->
                            selectionMode = true
                            selectedNotes.add(note)
                        },
                        selectedNotes = selectedNotes,
                        scope = scope,
                        drawerState = drawerState,
                    )
                }
            }
        ) { innerPadding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                ) {



                    Box(modifier = Modifier.fillMaxSize()) {
                        if (notesState.isEmpty()) {
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
                                    text = "No notes yet",
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
                                notes = notesState,
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


}




