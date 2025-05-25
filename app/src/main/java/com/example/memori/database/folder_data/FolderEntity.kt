/**
 * Represents a folder entity in the Room database.
 *
 * @property id Auto-generated primary key for the folder.
 * @property folderUuid Unique identifier for the folder, generated as a UUID.
 * @property folderName Name of the folder.
 * @property userId Identifier of the user who owns the folder; nullable.
 * @property lastModified Timestamp of the last modification to the folder, in milliseconds.
 * @property isProtected Indicates whether the folder is protected.
 * @property encryptedPin Encrypted PIN for folder protection; nullable.
 */
package com.example.memori.database.folder_data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "folders", indices = [Index(value = ["folder_uuid"], unique = true)])
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
    val lastModified: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_protected")
    val isProtected: Boolean = false,

    @ColumnInfo(name = "encrypted_pin")
    val encryptedPin: String? = null

)
