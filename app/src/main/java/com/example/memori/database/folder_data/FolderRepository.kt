/**
 * Repository class for managing folder data operations.
 *
 * @property userId The unique identifier of the currently authenticated Firebase user.
 * @property allFolders A Flow emitting the list of all folders associated with the current user.
 */

/**
 * Inserts a new folder into the database.
 *
 * @param folder The [FolderEntity] to be inserted.
 */

/**
 * Updates an existing folder in the database.
 *
 * @param folder The [FolderEntity] with updated information.
 */

/**
 * Updates the name and last modified timestamp of a folder.
 *
 * @param folderUuid The unique identifier of the folder to update.
 * @param folderName The new name for the folder.
 * @param newLastModified The new last modified timestamp (defaults to current system time).
 */

/**
 * Deletes a folder from the database.
 *
 * @param folderId The ID of the folder to delete.
 */

/**
 * Retrieves a folder by its UUID as a Flow.
 *
 * @param folderUuid The unique identifier of the folder.
 * @return A [Flow] emitting the [FolderEntity] with the specified UUID.
 *
 * This class provides an abstraction layer over the [FoldersDao] for accessing and modifying
 * folder-related data in the database. It also integrates with Firebase Authentication to
 * associate folder data with the currently authenticated user.
 *
 * @property foldersDao The Data Access Object for folder operations.
 */
package com.example.memori.database.folder_data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class FolderRepository(private val foldersDao: FoldersDao) {

    val userId = Firebase.auth.currentUser?.uid

    val allFolders: Flow<List<FolderEntity>> = foldersDao.getFolders(userId)

    suspend fun insert(folder: FolderEntity) = foldersDao.insertFolder(folder)

    suspend fun update(folder: FolderEntity) = foldersDao.updateFolder(folder)

    suspend fun updateFolderName(folderUuid: String, folderName: String, newLastModified: Long = System.currentTimeMillis()) =
        foldersDao.updateFolderName(folderUuid, folderName, newLastModified)

    suspend fun deleteFolder(folderId: Int) = foldersDao.deleteFolder(folderId = folderId)


    fun getFolderByUuid(folderUuid: String): Flow<FolderEntity> {
        return foldersDao.getFolderByUuid(folderUuid)
    }




}