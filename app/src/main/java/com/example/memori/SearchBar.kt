package com.example.memori

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.stateIn


@ExperimentalMaterial3Api
@Composable

fun SearchBarComponent(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(NoteDatabase.getDatabase(
                context = LocalContext.current
            ).noteDao())
        )
    ),
    modifier: Modifier
){

    val noteState = noteViewModel.allNotes.collectAsStateWithLifecycle(initialValue = emptyList())
    val notes = noteState.value

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val filteredNotes = noteViewModel.searchNotes(searchQuery).collectAsState(initial = emptyList()).value

    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    // Animazione della rotazione dell'icona
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f, // 90° quando si espande
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize().semantics { isTraversalGroup = true }
    ){
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter).semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = {expanded = it},
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = if(expanded) Icons.Filled.Close else Icons.Filled.Menu,
                            contentDescription = "Close",
                            modifier = Modifier.padding(8.dp).clickable {
                                expanded = false
                                searchQuery = ""
                            }.rotate(rotationAngle)
                        )
                    },


                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },

        ) {
            when {
                searchQuery.isNotEmpty() and filteredNotes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = randomKaomoji,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.Center)

                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "No notes found",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Center).padding(top = 90.dp)
                        )
                    }
                }
                searchQuery.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),

                        ) {
                        NoteCard(filteredNotes, navController)
                    }

                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

            }

        }
    }


}