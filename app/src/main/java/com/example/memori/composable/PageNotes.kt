package com.example.memori.composable

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NotesEntity
import com.example.memori.R
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@ExperimentalMaterial3Api
@Composable
fun ScreenNotes(
    navController: NavHostController,
    viewModelNote: NoteViewModel = viewModel(
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
    folderId: Int? = null,
    folderName: String? = null,
) {


    var text by remember { mutableStateOf<String>("") }
    var title by remember { mutableStateOf<String>("") }
    var noteId by remember { mutableStateOf<Int?>(null) }
    var favorite by remember { mutableStateOf<Boolean>(false) }
    var image by remember { mutableStateOf<String?>(null) }
    var imageOnNote by remember { mutableStateOf<String?>(null) }

    val allFolderAvailable by folderViewModel.allFolders.collectAsStateWithLifecycle()
    var selectedFolder by rememberSaveable { mutableStateOf<Int?>(null) }


    val coroutineScope = rememberCoroutineScope()


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showBottomSheetForWallpaper by remember { mutableStateOf(false) }
    var showBottomSheetForMoreOptions by remember { mutableStateOf(false) }
    val sheetStateForWallpaper = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val sheetStateForOptions = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )


    var showDialog by remember { mutableStateOf(false) }
    var showDialogForArchive by remember { mutableStateOf(false) }
    var showDialogForFolder by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val imagePath = saveWallpaperInLocally(context, it)
                imageOnNote = imagePath

                coroutineScope.launch {
                    delay(500)
                    saveNotes(
                        viewModelNote,
                        noteId,
                        title,
                        text,
                        favorite,
                        wallpaper = image,
                        image = imagePath,

                    ) { noteId = it }
                }
            }
        }





    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        image?.let { img ->
            val resIdImg = context.resources.getIdentifier(img, "drawable", context.packageName)

            val painter = if (resIdImg != 0) {
                painterResource(id = resIdImg)
            } else {
                rememberAsyncImagePainter(img)
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
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (folderId != null && folderName != null) {
                            navController.navigate("folderNotes/$folderId/$folderName") {
                                popUpTo("pageNotes") {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("pageNotes") {
                                    inclusive = true
                                }
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
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
                                viewModelNote,
                                noteId,
                                title,
                                text,
                                favorite,
                                image
                            ) { noteId = it }
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
                        Icon(Icons.Filled.Archive, contentDescription = "Menu")
                    }

                    IconButton(
                        onClick = {
                            showDialogForFolder = true
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = "Folder",
                            tint = if (selectedFolder != null || folderId != null) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.5f
                            ) else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },

        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                actions = {
                    IconButton(onClick = {
                        showBottomSheetForMoreOptions = true
                    }) {
                        Icon(Icons.Outlined.AddBox, contentDescription = "Show menu")

                    }
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

                },


                )

        },
        containerColor = Color.Transparent


    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            ) {


            if (!imageOnNote.isNullOrBlank()) {
                val resIdImg =
                    context.resources.getIdentifier(imageOnNote, "drawable", context.packageName)
                val painter = if (resIdImg != 0) {
                    painterResource(id = resIdImg)
                } else {
                    rememberAsyncImagePainter(imageOnNote)
                }
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                        .zIndex(-1f)
                        .clickable {
                            Log.e("Nav", "Immagine cliccata")
                            Log.e("Nav", "Id: $noteId")
                            navController.navigate("detailsImage/${Uri.encode(imageOnNote)}/${noteId}")
                        },
                    contentScale = ContentScale.Crop,
                )

            }

            OutlinedTextField(
                value = title,
                onValueChange = { newText ->
                    title = newText
                    coroutineScope.launch {
                        delay(500)
                        saveNotes(
                            viewModelNote,
                            noteId,
                            title,
                            text,
                            favorite,
                            image
                        ) { noteId = it}
                    }

                },
                placeholder = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,

                            ),
                        modifier = Modifier.padding(0.dp),
                        color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground

                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,


                    ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground

                ),
                modifier = Modifier.fillMaxWidth(),


                )
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    coroutineScope.launch {
                        delay(500)
                        saveNotes(
                            viewModelNote,
                            noteId,
                            title,
                            text,
                            favorite,
                            image
                        ) { noteId = it }
                    }
                },
                placeholder = {
                    Text(
                        text = "Type something...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,

                            ),
                        color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,


                    ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = if (image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,

                    ),

                )


        }

        if (showDialog) {
            GenericAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    coroutineScope.launch {
                        delay(500)
                        viewModelNote.delete(noteId ?: 0)
                        showDialog = false
                        navController.navigate("home") {
                            popUpTo("pageNotes") {
                                inclusive = true
                            }
                        }

                    }

                },
                dialogText = "Are you sure you want to delete this note?",
                dialogTitle = "Delete note",
                icon = Icons.Outlined.Delete,
            )

        }
        if (showDialogForArchive) {
            GenericAlertDialog(
                onDismissRequest = { showDialogForArchive = false },
                onConfirmation = {
                    coroutineScope.launch {
                        delay(500)
                        viewModelNote.archiveNote(noteId ?: 0)
                        showDialogForArchive = false
                        navController.navigate("home") {
                            popUpTo("pageNotes") {
                                inclusive = true
                            }
                        }
                    }
                },
                dialogText = "Are you sure you want to archive this note?",
                dialogTitle = "Archive note",
                icon = Icons.Outlined.Archive,
            )
        }

        if (showDialogForFolder) {
            AlertDialog(
                onDismissRequest = { showDialogForFolder = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (selectedFolder != null) {
                                coroutineScope.launch {
                                    delay(500)
                                    viewModelNote.moveNoteToFolder(noteId ?: 0, selectedFolder ?: 0)
                                    showDialogForFolder = false
                                }
                            } else {
                                showDialogForFolder = false
                            }
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
                        allFolderAvailable.forEach { folder ->
                            val isSelected = selectedFolder == folder.id || folder.id == folderId
                            Card(
                                onClick = {
                                    selectedFolder = folder.id
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

    }

    if (showBottomSheetForMoreOptions) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            sheetState = sheetStateForOptions,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetStateForOptions.hide()
                    showBottomSheetForMoreOptions = false
                }
            },
            tonalElevation = 2.dp,


            ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        showBottomSheetForMoreOptions = !showBottomSheetForMoreOptions
                        sheetStateForOptions.hide()
                        launcher.launch("image/*")

                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "View Images",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add image")
                }
            }
        }


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
                text = "Choice a wallpaper",
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
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        onClick = {
                            coroutineScope.launch {
                                image = null
                                saveNotes(
                                    viewModelNote,
                                    noteId,
                                    title,
                                    text,
                                    favorite
                                ) { noteId = it}
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
                                saveNotes(
                                    viewModelNote,
                                    noteId,
                                    title,
                                    text,
                                    favorite,
                                    selectedWallpaper
                                ) { noteId = it }
                                image = selectedWallpaper
                                Log.e("Wallpaper", selectedWallpaper)
                                sheetStateForWallpaper.hide()
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
}


fun saveNotes(
    viewModelNote: NoteViewModel,
    noteID: Int?,
    title: String,
    content: String,
    favorite: Boolean,
    wallpaper: String? = null,
    image: String? = null,
    onNoteSaved: (Int?) -> Unit

) {
    val isEmpty = title.isBlank()
            && content.isBlank()
            && wallpaper.isNullOrBlank()
            && image.isNullOrBlank()
    if (isEmpty) {
        noteID?.takeIf { it != 0 }?.let { viewModelNote.delete(it) }
        onNoteSaved(null)
        return
    }

    val note = NotesEntity(
        id = noteID ?: 0,
        title = title,
        content = content,
        favorite = favorite,
        image = wallpaper,
        pathImg = image,
    )

    if (noteID != null && noteID != 0) {
        viewModelNote.update(note)
        onNoteSaved(noteID)
    } else {
        // qui potresti lasciare Room generare l’ID se usi AutoIncrement,
        // altrimenti genera manualmente
        val newId = (0..Int.MAX_VALUE).random()
        viewModelNote.insert(note.copy(id = newId))
        onNoteSaved(newId)
    }
}

fun saveImageOnNote(
    viewModelNote: NoteViewModel,
    noteID: Int?,
    image: String,
    onImageSaved: (Int) -> Unit
) {

    val note = NotesEntity(
        id = noteID ?: 0,
        title = "",
        content = "",
        favorite = false,
        pathImg = image,
    )

    if (noteID == null || noteID == 0) {
        val newID = (0..Int.MAX_VALUE).random()
        viewModelNote.insert(note.copy(id = newID))
        onImageSaved(newID)
    } else {
        viewModelNote.update(note)
        onImageSaved(noteID)
    }
}


fun saveWallpaperInLocally(context: Context, uri: Uri): String? {
    val file = File(context.filesDir, "wallpaper/${System.currentTimeMillis()}.png")
    file.parentFile?.mkdirs()

    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null

    }
}