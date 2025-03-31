package com.example.memori

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

    var isCheckItem by rememberSaveable { mutableStateOf<Boolean>(false) }
    var checkListId by rememberSaveable { mutableStateOf<Int?>(null) }

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

    var isCheckBoxSelected by remember { mutableStateOf(false) }

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
                                saveNotes(db, noteId, title, text, favorite) {id -> noteId = id}
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
                            saveNotes(db, noteId, title, text, favorite) {id -> noteId = id}
                        }
                    },
                    placeholder = { Text(
                        text = "Title",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,

                        ),
                        color = if(image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground

                    )},
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = if(image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,



                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = if(image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground

                    ),
                    modifier = Modifier.fillMaxWidth(1f)

                )
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        newText -> text = newText
                        coroutineScope.launch {
                            saveNotes(db, noteId, title, text, favorite) {id -> noteId = id}
                        }
                    },
                    placeholder = { Text(
                        text = "Type something...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,

                        ),
                        color = if(image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground
                    )},
                    modifier = Modifier.fillMaxWidth(1f)
                        .padding(top = 35.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,  // Rimuove il bordo quando disabilitato
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = if(image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,



                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        color = if(image == "wallpaper_4") Color.White else MaterialTheme.colorScheme.onBackground,

                    ),




                )
            }

        }

    if(showBottomSheetForMoreOptions){
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            sheetState = sheetStateForOptions,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetStateForOptions.hide()
                    showBottomSheetForMoreOptions = false
                }
            },
            tonalElevation = 2.dp,


        ) {
            Column(
                modifier = Modifier.padding(16.dp).align(Alignment.Start)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.CheckBox,
                        contentDescription = "Check box",
                        modifier = Modifier.padding(end = 8.dp).clickable {
                            coroutineScope.launch {
                                isCheckBoxSelected = !isCheckBoxSelected
                                sheetStateForOptions.hide()
                            }
                        }
                    )
                    Text(
                        text = "Add Checkboxes",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Image,
                        contentDescription = "Add image",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Add image",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }

    if (showBottomSheetForWallpaper) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
                R.drawable.wallpaper_1,
                R.drawable.wallpaper_2,
                R.drawable.wallpaper_3,
                R.drawable.wallpaper_4,
                R.drawable.wallpaper_5,
                R.drawable.wallpaper_6,
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
                                sheetStateForWallpaper.hide()
                                showBottomSheetForWallpaper = false
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

suspend fun saveCheckListItem(
    db: NoteDatabase,
    checkListID: Int?,
    content: String,
    isChecked: Boolean,
    onCheckListItemSaved: (Int) -> Unit
){
    val checkListDao = db.checkListDao()

    val checkListItem = CheckListNoteEntity(id = checkListID ?: 0, checkListId = checkListID ?: 0, item = content, isChecked = isChecked)

    if(checkListID == null || checkListID == 0){
        val newID: Long = checkListDao.insert(checkListItem)
        onCheckListItemSaved(newID.toInt())
    } else {
        checkListDao.updateCheckListItem(checkListItem)
        onCheckListItemSaved(checkListID)
    }
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













