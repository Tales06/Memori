/**
 * Entity class representing a note in the database.
 *
 * @property id Unique identifier for the note. Auto-generated primary key.
 * @property title Title of the note.
 * @property content Content/body of the note.
 * @property favorite Indicates if the note is marked as favorite.
 * @property image Optional path or URI to the note's wallpaper image.
 * @property lastModified Timestamp of the last modification (in milliseconds).
 * @property isDeleted Indicates if the note is marked as deleted (soft delete).
 * @property archivedNote Indicates if the note is archived.
 * @property folderId Optional foreign key referencing the folder containing this note.
 */
package com.example.memori.database.note_data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.memori.database.folder_data.FolderEntity

@Entity(
    tableName = "notes",


)
data class NotesEntity(

    //annotazioni per definire la chiave primaria e il nome della colonna nel database
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //annotazione per definire il nome della colonna nel database
    @ColumnInfo(name = "title")
    val title: String = "",

    //annotazione per definire il nome della colonna nel database
    @ColumnInfo(name = "content")
    val content: String = "",

    @ColumnInfo(name = "favorite")
    val favorite: Boolean = false,

    @ColumnInfo(name = "wallpaper")
    val image: String? = null,


    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis(),


    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "archivedNote")
    val archivedNote: Boolean = false,

    @ColumnInfo(name = "folderId")
    val folderId: Int? = null
)




