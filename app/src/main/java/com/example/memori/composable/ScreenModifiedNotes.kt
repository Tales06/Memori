/**
 * Composable screen for viewing and editing a modified note.
 *
 * This screen allows users to:
 * - View and edit the title and content of a note.
 * - Mark the note as favorite.
 * - Change the note's wallpaper/background.
 * - Archive or unarchive the note.
 * - Move the note to a folder or remove it from a folder.
 * - Delete the note.
 *
 * The screen supports navigation back to the home screen or to a specific folder's notes.
 * It uses dialogs for confirmation actions (delete, archive, move to folder) and a bottom sheet for wallpaper selection.
 *
 * @param id The unique identifier of the note to be displayed and edited.
 * @param navController The navigation controller used for navigating between screens.
 * @param viewModel The [NoteViewModel] instance for managing note data. Defaults to a new instance with the appropriate factory.
 * @param folderViewModel The [FolderViewModel] instance for managing folder data. Defaults to a new instance with the appropriate factory.
 * @param folderId (Optional) The ID of the folder the note belongs to, if any.
 * @param folderName (Optional) The name of the folder the note belongs to, if any.
 *
 * @see NoteViewModel
 * @see FolderViewModel
 */
package com.example.memori.composable

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import coil.compose.rememberAsyncImagePainter
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesRepository
import com.example.memori.R
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun ScreenModifiedNotes(
    id: Int,
    navController: NavHostController,
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
                NoteDatabase.getDatabase(LocalContext.current).folderDao()
            )
        )
    ),
    folderId: Int? = null,
    folderName: String? = null,
    /**
     * ricordarsi che quando NoteViewModel nn viene creata l'instanza bisogna richiamarlo automaticamente con il viewModel stesso
     */

) {
    val note = viewModel.getNoteById(id).collectAsStateWithLifecycle(initialValue = null).value

    val allFolderAvailable by folderViewModel.allFolders.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf<String>("") }
    var noteId by remember { mutableStateOf<Int?>(null) }
    var favorite by remember { mutableStateOf<Boolean>(false) }
    var image by remember { mutableStateOf<String?>(null) }
    var isArchived by remember { mutableStateOf<Boolean>(false) }
    var folderIdFK by remember { mutableStateOf<Int?>(null) }


    val coroutineScope = rememberCoroutineScope()

    var isInitialLoad by remember { mutableStateOf(false) }

    var showBottomSheetForWallpaper by remember { mutableStateOf(false) }
    val sheetStateForWallpaper = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showDialog by remember { mutableStateOf(false) }
    var showDialogForArchive by remember { mutableStateOf(false) }
    var showDialogForFolder by remember { mutableStateOf(false) }

    val context = LocalContext.current


    // For focus management
    // This is used to request focus on the content field when the screen is loaded
    val contentFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(note) {
        if (!isInitialLoad) {

            note?.let {
                title = it.title
                content = it.content
                noteId = it.id
                favorite = it.favorite
                image = it.image
                isInitialLoad = true
                isArchived = it.archivedNote
                folderIdFK = folderId ?: it.folderId
            }


        }
    }

    // For image loading
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        image?.let { img ->
            val resIdImg = context.resources.getIdentifier(img, "drawable", context.packageName)

            val painter = if (resIdImg != 0) {
                painterResource(id = resIdImg)
            } else {
                rememberAsyncImagePainter(resIdImg)
            }

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,

                    ),

                navigationIcon = {
                    IconButton(
                        onClick = {
                            if(folderId != null && folderName != null) {
                                navController.navigate("folderNotes/$folderId/$folderName") {
                                    popUpTo("modifiedNotes/${id}/$folderId/$folderName") {
                                        inclusive = true
                                    }
                                }
                            } else {
                                 navController.navigate("home") {
                                     popUpTo("modifiedNotes/$id") {
                                         inclusive = true
                                     }
                                 }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }

                },
                title = {
                    Text(text = "")
                },
                actions = {
                    IconButton(onClick = {
                        favorite = !favorite
                        coroutineScope.launch {
                            delay(500)
                            saveNotes(
                                viewModel,
                                noteId,
                                title,
                                content,
                                favorite,
                                image,
                                folderId = folderIdFK
                            ) { id -> noteId = id }
                        }
                    }) {
                        Icon(
                            imageVector = if (favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            tint = Color.Red.copy(alpha = 0.5f),
                            contentDescription = "Favorite"
                        )
                    }
                    IconButton(onClick = {
                        showDialogForArchive = true
                    }) {
                        Icon(
                            Icons.Filled.Archive,
                            contentDescription = "Archive",
                            tint = if (isArchived) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = {
                        showDialogForFolder = true
                    }) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = "Folder",
                            tint = if (folderIdFK != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onBackground
                        )
                    }

                },
            )
        },
        bottomBar = {
            BottomAppBar(

                containerColor = Color.Transparent,
                contentColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,
                actions = {
                    IconButton(onClick = {
                        showBottomSheetForWallpaper = true
                    }) {
                        Icon(Icons.Outlined.Palette, contentDescription = "Change theme")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            showDialog = true
                        },
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Show other menu")
                    }
                }
            )


        },
        containerColor = Color.Transparent,

        ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    contentFocusRequester.requestFocus()
                    keyboardController?.show()
                }
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    coroutineScope.launch {
                        delay(500)
                        saveNotes(
                            viewModel,
                            noteId,
                            title,
                            content,
                            favorite,
                            image,
                            folderId = folderIdFK
                        ) { id -> noteId = id }
                    }
                },

                placeholder = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground

                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(contentFocusRequester)

            )
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    coroutineScope.launch {
                        delay(500)
                        saveNotes(
                            viewModel,
                            noteId,
                            title,
                            content,
                            favorite,
                            image,
                            folderId = folderIdFK
                        ) { id -> noteId = id }
                    }
                },
                placeholder = {
                    Text(
                        text = "Type something...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp
                        ),
                        color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -16.dp)
                    .focusRequester(contentFocusRequester),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground

                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground
                )
            )


        }

        if (showBottomSheetForWallpaper) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                sheetState = sheetStateForWallpaper,
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetStateForWallpaper.hide()
                        showBottomSheetForWallpaper = false
                    }
                },

                tonalElevation = 2.dp
            ) {
                Text(
                    text = "Wallpaper",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                val wallpapers = listOf(
                    R.drawable.wallpaper_2,
                    R.drawable.wallpaper_3,
                    R.drawable.wallpaper_5,
                    R.drawable.wallpaper_6,
                )

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp)

                ) {
                    item {
                        ElevatedCard(
                            modifier = Modifier.size(100.dp).padding(8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                            onClick = {
                                coroutineScope.launch {
                                    image = null
                                    saveNotes(
                                        viewModel,
                                        noteId,
                                        title,
                                        content,
                                        favorite,
                                        folderId = folderIdFK
                                    ) { id -> noteId = id }
                                    sheetStateForWallpaper.hide()
                                    showBottomSheetForWallpaper = false
                                }
                            }

                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.HideImage,
                                    contentDescription = "Hide image",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }

                        }
                    }

                    items(wallpapers) { wallpaper ->
                        ElevatedCard(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                            onClick = {
                                val selectedWallpaper =
                                    context.resources.getResourceEntryName(wallpaper)
                                coroutineScope.launch {
                                    delay(500)
                                    saveNotes(
                                        viewModel,
                                        noteId,
                                        title,
                                        content,
                                        favorite,
                                        selectedWallpaper,
                                        folderId = folderIdFK
                                    ) { id -> noteId = id }
                                    image = selectedWallpaper
                                    Log.e("Wallpaper", selectedWallpaper)
                                    showBottomSheetForWallpaper = false
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(id = wallpaper),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop // Adatta l'immagine al contenitore
                                )
                            }

                        }
                    }
                }

            }
        }
        if (showDialog) {
            GenericAlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                dialogTitle = "Delete note",
                dialogText = "Are you sure you want to delete this note?",
                onConfirmation = {
                    coroutineScope.launch {
                        delay(500)
                        viewModel.delete(noteId!!)
                        showDialog = false
                        navController.navigate("home") {
                            popUpTo("modifiedNotes/{id}") {
                                inclusive = true
                            }
                        }
                    }
                },
                icon = Icons.Outlined.Delete,
            )
        }
        if (showDialogForArchive) {
            GenericAlertDialog(
                dialogTitle = "Archive note",
                dialogText = if (isArchived) "Are you sure you want to unarchive this note?" else "Are you sure you want to archive this note?",
                onDismissRequest = {
                    showDialogForArchive = false
                },
                onConfirmation = {
                    coroutineScope.launch {
                        delay(500)
                        if (isArchived) {
                            viewModel.unArchiveNote(noteId!!)
                        } else {
                            viewModel.archiveNote(noteId!!)
                            viewModel.clearNoteFolder(noteId!!)
                        }
                        showDialogForArchive = false
                        navController.navigate("home") {
                            popUpTo("modifiedNotes/{id}") {
                                inclusive = true
                            }
                        }
                    }
                },
                icon = if (isArchived) Icons.Outlined.Archive else Icons.Filled.Archive,
            )
        }
        if (showDialogForFolder) {
            AlertDialog(
                onDismissRequest = { showDialogForFolder = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (folderIdFK == null) {
                                coroutineScope.launch {
                                    delay(500)
                                    viewModel.clearNoteFolder(noteId ?: 0)
                                }
                            } else {
                                coroutineScope.launch {
                                    delay(500)
                                    viewModel.moveNoteToFolder(noteId ?: 0, folderIdFK ?: 0)
                                    viewModel.unArchiveNote(noteId!!)
                                }
                            }
                            showDialogForFolder = false
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialogForFolder = false
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                },
                title = {
                    Text(
                        text = "Move note to folder",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                text = {
                    Column {
                        Card(
                            onClick = { folderIdFK = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = if (folderIdFK == null && folderId != null)
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
                        allFolderAvailable.forEach { folder ->
                            val isSelected = folderIdFK == folder.id
                            Card(
                                onClick = {
                                    folderIdFK = folder.id
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (isSelected) 8.dp else 0.dp
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
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
    }

}




