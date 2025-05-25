package com.example.memori.database.folder_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface FoldersDao {

    @Upsert
    suspend fun insertFolder(folder: FolderEntity): Long

    @Query("SELECT * FROM folders WHERE user_id IS NULL OR user_id = :userId")
    fun getFolders(userId: String? = null): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE folder_uuid = :folderUuid")
    fun getFolderByUuid(folderUuid: String): Flow<FolderEntity>

    @Update
    suspend fun updateFolder(folder: FolderEntity)


    @Query("DELETE FROM folders WHERE folder_uuid = :folderUuid OR id = :folderId")
    suspend fun deleteFolder(folderUuid: String? = null, folderId: Int? = null)

    @Query("UPDATE folders SET folder_name = :folderName, last_modified = :newLastModified WHERE folder_uuid = :folderUuid")
    suspend fun updateFolderName(folderUuid: String, folderName: String, newLastModified: Long = System.currentTimeMillis())


}