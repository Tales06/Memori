/**
 * Composable function that displays a customizable search bar with note filtering and selection capabilities.
 *
 * This component allows users to search through notes, select multiple notes for batch deletion,
 * and navigate to a note modification screen. It also handles UI states such as showing a random kaomoji
 * and a "No notes found" message when the search yields no results.
 *
 * @param navController The NavController used for navigation between screens.
 * @param noteViewModel The ViewModel for managing note data. Defaults to a ViewModel created with a factory.
 * @param modifier Modifier to be applied to the SearchBarComponent.
 * @param expanded Boolean indicating whether the search bar is expanded.
 * @param onExpandedChange Callback invoked when the expanded state changes.
 * @param onNoteClick Callback invoked when a note is clicked.
 * @param onNoteLongPress Callback invoked when a note is long-pressed.
 * @param drawerState The state of the navigation drawer.
 * @param scope CoroutineScope for launching suspend functions (e.g., opening the drawer).
 *
 * Features:
 * - Animated leading icon (menu/close) with rotation.
 * - Search input with real-time filtering of notes.
 * - Lazy vertical grid displaying filtered notes with support for selection and long-press actions.
 * - Batch deletion of selected notes with a confirmation toast.
 * - Displays a random kaomoji and message when no notes are found.
 * - Handles navigation to note modification screen on note click.
 */
package com.example.memori.composable

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun SearchBarComponent(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            context = LocalContext.current,
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
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val context = LocalContext.current

    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredNotes =
        noteViewModel.searchNotes(searchQuery).collectAsState(initial = emptyList()).value

    val randomKaomoji by remember { mutableStateOf(kaomoji.random()) }

    // State for selected notes
    val selectedNotesInSearch = remember { mutableStateListOf<NotesEntity>() }

    // Animate the rotation of the leading icon based on the expanded state
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    val leadingIconForSearchBar = if (expanded) Icons.Filled.Close else Icons.Filled.Menu


    val listState = rememberLazyGridState()


    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .semantics { traversalIndex = 0f },
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { searchQuery = it }, // Update search query
                onSearch = { onExpandedChange(false) }, // Close search bar on search
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
                                // Toggle the search bar state
                                if (expanded) {
                                    onExpandedChange(false)
                                    searchQuery = ""
                                    selectedNotesInSearch.clear()
                                } else {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            }
                            .rotate(rotationAngle)
                    )


                },
                trailingIcon = {

                    // Show the delete icon only if there are selected notes
                    if (selectedNotesInSearch.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                selectedNotesInSearch.forEach { note ->
                                    noteViewModel.delete(note.id)
                                }
                                Toast.makeText(
                                    context,
                                    "${selectedNotesInSearch.size} notes deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedNotesInSearch.clear()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete selected notes",
                            )
                        }
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        when {
            searchQuery.isNotEmpty() && filteredNotes.isEmpty() -> {
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
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    state = listState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        val isSelected = selectedNotesInSearch.any { it.id == note.id }
                        val resIdImg = note.image?.let {
                            context.resources.getIdentifier(it, "drawable", context.packageName)
                        } ?: 0

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(
                                2.dp, MaterialTheme.colorScheme.primary
                            ) else null,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(8.dp)
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 200.dp)
                                .combinedClickable(
                                    indication = ripple(),
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (selectedNotesInSearch.isNotEmpty()) {
                                            if (isSelected)
                                                selectedNotesInSearch.removeIf { it.id == note.id }
                                            else
                                                selectedNotesInSearch.add(note)
                                        } else {
                                            navController.navigate("modifiedNotes/${note.id}") {
                                                launchSingleTop = true
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        if (isSelected)
                                            selectedNotesInSearch.removeIf { it.id == note.id }
                                        else
                                            selectedNotesInSearch.add(note)
                                    }
                                )
                        ) {
                            Box {
                                if (resIdImg != 0) {
                                    Image(
                                        painter = painterResource(id = resIdImg),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .heightIn(min = 150.dp)
                                    )
                                }
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = note.content.take(50).let { if (it.length == 50) "$it…" else it },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                ) { /* nothing */ }
            }
        }
    }
}
