package com.example.memori.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesEntity
import com.example.memori.database.note_data.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable

fun SearchBarComponent(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(
                    context = LocalContext.current
                ).noteDao()
            )
        )
    ),
    modifier: Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedNotes: List<NotesEntity>,
    onNoteClick: (NotesEntity) -> Unit,
    onNoteLongPress: (NotesEntity) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope

) {


    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredNotes =
        noteViewModel.searchNotes(searchQuery).collectAsState(initial = emptyList()).value

    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }


    // Animazione della rotazione dell'icona
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f, // 90° quando si espande
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val leadingIconForSearchBar = if (expanded) Icons.Filled.Close else Icons.Filled.Menu


    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .semantics { traversalIndex = 0f },
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { onExpandedChange(false) },
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = leadingIconForSearchBar,
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                if (expanded) {
                                    onExpandedChange(false)
                                    searchQuery = ""
                                } else {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            }
                            .rotate(rotationAngle)
                    )
                },

                )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,

        ) {
        when {
            searchQuery.isNotEmpty() and filteredNotes.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                ) {
                    Text(
                        text = randomKaomoji,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.CenterHorizontally)

                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "No notes found",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 90.dp)
                    )
                }
            }

            searchQuery.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),

                    ) {
                    NoteCard(
                        filteredNotes,
                        navController,
                        onNoteLongPress = onNoteLongPress,
                        onNoteClick = onNoteClick,
                        selectedNotes = selectedNotes,

                        )
                }

            }

            else -> {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()) {

                }
            }

        }

    }


}

