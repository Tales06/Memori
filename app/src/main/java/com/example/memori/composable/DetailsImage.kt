package com.example.memori.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesRepository
import kotlinx.coroutines.launch
import java.net.URLDecoder

@Composable
@ExperimentalMaterial3Api
fun DetailsImage(
    pathImg: String,
    id: Int,
    viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                noteDao = NoteDatabase.getDatabase(LocalContext.current).noteDao()
            )
        )
    ),
    navController: NavHostController
) {

    var showOptions by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    //decodfica path

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("modifiedNotes/$id") }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showOptions = !showOptions
                    }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "options")
                        Box(modifier = Modifier.padding(16.dp)) {
                            DropdownMenu(
                                expanded = showOptions,
                                onDismissRequest = { showOptions = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.deletePathImg(id)
                                            if(viewModel.getNoteById(id).value?.content.isNullOrBlank() && viewModel.getNoteById(id).value?.title.isNullOrBlank()){
                                                viewModel.delete(id)
                                            }
                                            navController.navigate("modifiedNotes/$id")
                                        }
                                    }
                                )

                            }
                        }
                    }
                },
                title = { Text("") },

                )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            // Decode the pathImg
            println("pathImg: $pathImg")
            val decodedPath = URLDecoder.decode(pathImg, "UTF-8")
            val painter = rememberAsyncImagePainter(decodedPath)
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.Center),
                contentDescription = null
            )

        }
    }
}