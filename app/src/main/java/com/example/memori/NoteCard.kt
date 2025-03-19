package com.example.memori

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow

@Composable
fun NoteCard(notes: List<NotesEntity>, navController: NavController) {

    val context = LocalContext.current
    val listState = rememberLazyGridState()



    Surface(
        modifier = Modifier.fillMaxSize(), contentColor = MaterialTheme.colorScheme.background
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
                val scrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

                // Calcola il fattore di opacità in base alla posizione della nota
                val alpha by animateFloatAsState(
                    targetValue = if (index < firstVisibleItemIndex) 1.5f
                    else 1.3f - (index - firstVisibleItemIndex) * 0.095f,
                    label = "Fade Animation"
                )
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .graphicsLayer { this.alpha = alpha.coerceIn(0f, 1f)} //applico la dissolvenza
                        .fillMaxWidth()
                        .heightIn(max = 200.dp, min = 120.dp),
                    onClick = {
                        note.id.let { id ->

                            navController.navigate("modifiedNotes/$id")
                        } ?: Log.e("NoteCard", "Note ID is null")
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box {
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = note.content.take(50) + "...", // Limita il testo per migliorare la UI
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                }
            }
        }
    }

}
