/**
 * Data Access Object (DAO) for the Folders table.
 * Provides methods for inserting, querying, updating, and deleting folder records.
 */
package com.example.memori.database.folder_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) for performing operations on the folders table.
 *
 * Provides methods to insert, query, update, and delete folder entities.
 */

/**
 * Inserts a folder into the database. If a folder with the same primary key exists, it will be replaced.
 *
 * @param folder The [FolderEntity] to insert or update.
 * @return The row ID of the inserted folder.
 */

/**
 * Retrieves a list of folders for a specific user or all folders if userId is null.
 *
 * @param userId The ID of the user whose folders to retrieve, or null to retrieve all folders.
 * @return A [Flow] emitting the list of [FolderEntity] objects.
 */

/**
 * Retrieves a folder by its unique UUID.
 *
 * @param folderUuid The UUID of the folder to retrieve.
 * @return A [Flow] emitting the [FolderEntity] with the specified UUID.
 */

/**
 * Updates the details of an existing folder in the database.
 *
 * @param folder The [FolderEntity] with updated information.
 */

/**
 * Deletes a folder from the database by its UUID or ID.
 *
 * @param folderUuid The UUID of the folder to delete (optional).
 * @param folderId The ID of the folder to delete (optional).
 */

/**
 * Updates the name and last modified timestamp of a folder.
 *
 * @param folderUuid The UUID of the folder to update.
 * @param folderName The new name for the folder.
 * @param newLastModified The new last modified timestamp (defaults to current time).
 *
 * Provides methods to insert, query, update, and delete folder entities in the database.
 */
/**
 * Data Access Object (DAO) interface for performing database operations related to folders.
 * Define methods for querying, inserting, updating, and deleting folder entities in the database.
 */
@Dao
interface FoldersDao {

    /**
     * Inserts the given entity into the database if it does not exist, or updates it if it does.
     * This operation is performed atomically.
     */
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