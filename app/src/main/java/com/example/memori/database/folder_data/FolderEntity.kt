package com.example.memori.database.folder_data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "folders")
data class FolderEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "folder_uuid")
    val folderUuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "folder_name")
    val folderName: String = "",

    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis()

)
