package com.example.memori

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "notes")
data class NotesEntity(

    //annotazioni per definire la chiave primaria e il nome della colonna nel database
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //annotazione per definire il nome della colonna nel database
    @ColumnInfo(name = "title")
    val title: String,

    //annotazione per definire il nome della colonna nel database
    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "favorite")
    val favorite: Boolean,

    @ColumnInfo(name = "wallpaper")
    val image: String? = null

)

@Entity(
    tableName = "checklist_item",
    foreignKeys = [ForeignKey(
        entity = NotesEntity::class,
        parentColumns = ["id"],
        childColumns = ["checkList_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["checkList_id"])]
)
data class CheckListNote(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "checkList_id")
    val checkListId: Int,

    @ColumnInfo(name = "item")
    val item: String,

    @ColumnInfo(name = "isChecked")
    val isChecked: Boolean = false
)


