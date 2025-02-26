package com.example.memori

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun NoteCard(notes: List<NotesEntity>, navController: NavController){

    val context = LocalContext.current



    Surface(
        modifier = Modifier.fillMaxSize(),
        contentColor = MaterialTheme.colorScheme.background
    ){
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ){
            items(notes) {note ->
                val resIdImg = context.resources.getIdentifier(note.image, "drawable", context.packageName)
                val painter = if(resIdImg != 0){
                    painterResource(id = resIdImg)
                } else {
                    painterResource(id = R.drawable.wallpaper_1)
                }
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(8.dp).graphicsLayer { alpha = 0.99f }.size(200.dp),
                    onClick = {
                        note.id.let { id ->

                            navController.navigate("modifiedNotes/$id")
                        } ?: Log.e("NoteCard", "Note ID is null")
                    },

                ) {
                    Box{
                        Image(
                            painter = painter,
                            contentDescription = "Note image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().heightIn(150.dp)
                        )

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {



                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(text = note.id.toString(), style = MaterialTheme.typography.bodyLarge)
                            Text(text = note.favorite.toString(), style = MaterialTheme.typography.bodyLarge)
                            Text(text = note.image.toString(), style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                }
            }
        }
    }

}
