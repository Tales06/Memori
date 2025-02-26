package com.example.memori

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@SuppressLint("SuspiciousIndentation")
@ExperimentalMaterial3Api
@Composable
fun ScreenNotes(navController: NavHostController){
    val db = NoteDatabase.getDatabase(context = LocalContext.current)

    var text by remember { mutableStateOf<String>("") }
    var title by remember { mutableStateOf<String>("") }
    var noteId by remember { mutableStateOf<Int?>(null) }
    var favorite by remember { mutableStateOf<Boolean>(false) }
    var image by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val snackHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val context = LocalContext.current



        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            image?.let{ img ->
                val resIdImg = context.resources.getIdentifier(img, "drawable", context.packageName)

                val painter = if(resIdImg != 0){
                    painterResource(id = resIdImg)
                } else {
                    painterResource(id = R.drawable.wallpaper_1)
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
                        IconButton(onClick = { navController.navigate("home") }) {
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
                                saveNotes(db, noteId, title, text, favorite, image) {id -> noteId = id}
                            }

                        }){
                            Icon(imageVector = if(favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, tint = Color.Red.copy(alpha = 0.5f), contentDescription = "Favorite")

                        }
                        IconButton(onClick = {/*to do*/}){
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },

            bottomBar = {
                BottomAppBar (
                    containerColor = Color.Transparent,
                    actions = {
                        IconButton(onClick = {/*Do something*/}) {
                            Icon(Icons.Outlined.AddBox, contentDescription = "Show menu")

                        }
                        IconButton(onClick = {
                            showBottomSheet = true

                        }) {
                            Icon(Icons.Outlined.Palette, contentDescription = "Change theme")

                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {/*Do something*/},

                        ) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Show other menu")
                        }


                    },
                )

            },
            containerColor = Color.Transparent


        ) {
            innerPadding -> Box(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
            ){



                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        newText -> title = newText
                        coroutineScope.launch {
                            saveNotes(db, noteId, title, text, favorite, image) {id -> noteId = id}
                        }
                    },
                    placeholder = { Text(
                        text = "Title",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,

                        ),
                        color = if(image == "wallpaper_3") Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface

                    )},
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = if(image == "wallpaper_3") Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,



                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = if(image == "wallpaper_3") Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface

                    ),
                    modifier = Modifier.fillMaxWidth(1f)

                )
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        newText -> text = newText
                        coroutineScope.launch {
                            saveNotes(db, noteId, title, text, favorite, image) {id -> noteId = id}
                        }
                    },
                    placeholder = { Text(
                        text = "Type something...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,

                        ),
                        color = if(image == "wallpaper_3") Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                    )},
                    modifier = Modifier.fillMaxWidth(1f)
                        .padding(top = 35.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = if(image == "wallpaper_3") Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,



                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        color = if(image == "wallpaper_3") Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,

                    ),


                )
            }
        }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            },

            tonalElevation = 2.dp
        ) {
            Text(
                text = "Choice a wallpaper",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            val wallpapers = listOf(
                R.drawable.wallpaper_1,
                R.drawable.wallpaper_2,
                R.drawable.wallpaper_3,
            )

            LazyRow (
                modifier = Modifier.padding(horizontal = 16.dp)
            ){
                items(wallpapers){
                    wallpaper ->
                    ElevatedCard(
                        modifier = Modifier.size(100.dp)
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        onClick = {
                            val selectedWallpaper = context.resources.getResourceEntryName(wallpaper)
                            coroutineScope.launch {
                                saveNotes(db, noteId, title, text, favorite, selectedWallpaper) {id -> noteId = id}
                                image = selectedWallpaper
                                Log.e("Wallpaper", selectedWallpaper)
                                showBottomSheet = false
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ){
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



suspend fun saveNotes(
    db: NoteDatabase,
    noteID: Int?,
    title: String,
    content: String,
    favorite: Boolean,
    wallpaper: String? = null,
    onNoteSaved: (Int) -> Unit

){
    val noteDao = db.noteDao()
    val note = NotesEntity(id = noteID ?: 0, title = title, content = content, favorite = favorite, image = wallpaper)
    if(noteID == null || noteID == 0){
        val newID: Long = noteDao.insert(note)
        onNoteSaved(newID.toInt())

    } else {
        noteDao.update(note)
        onNoteSaved(noteID)
    }
}

suspend fun updateNoteWallpaper(
    db: NoteDatabase,
    noteID: Int,
    wallpaper: String,
){
    val noteDao = db.noteDao()

    val noteFlow = noteDao.getNoteById(noteID ?: return)
    val note = noteFlow.first() ?: return

    val updateNote = note.copy(image = wallpaper)
    noteDao.update(updateNote)
}

fun saveWallpaperInLocally(context: Context, uri: Uri): String?{
    val file = File(context.filesDir, "wallpaper/${System.currentTimeMillis()}.png")
    file.parentFile?.mkdirs()

    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: IOException){
        e.printStackTrace()
        null

    }
}






