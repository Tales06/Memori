/**
 * Displays a grid of note cards using Jetpack Compose.
 *
 * @param notes The list of [NotesEntity] objects to display.
 * @param navController The [NavController] used for navigation actions.
 * @param onNoteClick Callback invoked when a note is tapped.
 * @param onNoteLongPress Callback invoked when a note is long-pressed.
 * @param selectedNotes Optional list of selected [NotesEntity] objects for selection state.
 *
 * Each note card displays the note's title, a truncated version of its content, and an optional image.
 * Selected notes are visually highlighted with a border and a different background color.
 * Cards animate their opacity based on their position in the grid for a fade-in effect.
 */
package com.example.memori.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import coil.compose.rememberAsyncImagePainter
import com.example.memori.database.note_data.NotesEntity

@Composable
fun NoteCard(
    notes: List<NotesEntity>,
    navController: NavController,
    onNoteClick: (NotesEntity) -> Unit,
    onNoteLongPress: (NotesEntity) -> Unit,
    selectedNotes: List<NotesEntity>? = null,
) {

    val context = LocalContext.current
    val listState = rememberLazyGridState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 150.dp),
            state = listState,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(notes) { note ->

                val resIdImg = if (!note.image.isNullOrBlank()) {
                    context.resources.getIdentifier(note.image, "drawable", context.packageName)
                } else {
                    0
                }


                val index = notes.indexOf(note)
                val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

                // Animate the alpha based on the position of the item in the grid
                val alpha by animateFloatAsState(
                    targetValue = if (index < firstVisibleItemIndex) 1.5f
                    else 1.3f - (index - firstVisibleItemIndex) * 0.095f,
                    label = "Fade Animation"
                )

                val isSelected = selectedNotes?.contains(note)

                val borderColorCard = if (isSelected == true) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = borderColorCard.copy(alpha = if (isSelected == true) 0.2f else 1f)
                    ),
                    border = if (isSelected == true) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        null
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .graphicsLayer {
                            this.alpha = alpha.coerceIn(0f, 1f)
                        } //applico la dissolvenza
                        .fillMaxWidth()
                        .heightIn(max = 200.dp, min = 120.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onNoteClick(note) },
                                onLongPress = { onNoteLongPress(note) },
                            )
                        },

                    ) {
                    Box {

                        //Show the image if it exists
                        if (resIdImg != 0) {
                            Image(
                                painter = painterResource(id = resIdImg),
                                contentDescription = "Note image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .heightIn(150.dp)
                            )
                        }


                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = note.content.take(50) + "...", // Limit content for better UI
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )

                        }
                    }

                }

            }
        }
    }

}


