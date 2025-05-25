package com.example.memori.sync

import com.example.memori.database.folder_data.FolderEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository class for managing folder-related operations in Firestore.
 *
 * @property firestore The instance of [FirebaseFirestore] used to interact with Firestore.
 * By default, it uses [FirebaseFirestore.getInstance()].
 */
class FirestoreFolderRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Repository for managing folder data in Firestore for a specific user.
     *
     * @property firestore The Firestore instance used for database operations.
     */

    /**
     * Uploads a list of folders to Firestore under the specified user.
     *
     * @param userId The ID of the user whose folders are being uploaded.
     * @param folders The list of [FolderEntity] objects to upload.
     */

    /**
     * Retrieves all folders for a user from Firestore.
     *
     * @param userId The ID of the user whose folders are being retrieved.
     * @return A list of [FolderEntity] objects fetched from Firestore.
     */

    /**
     * Deletes a folder from Firestore for the specified user.
     *
     * @param userId The ID of the user whose folder is being deleted.
     * @param folderUuid The unique identifier of the folder to delete.
     */

    /**
     * Uploads a single folder to Firestore under the specified user.
     *
     * @param userId The ID of the user whose folder is being uploaded.
     * @param folder The [FolderEntity] object to upload.
     */

    /**
     * Renames a folder and updates its last modified timestamp in Firestore.
     *
     * @param userId The ID of the user whose folder is being renamed.
     * @param folderUuid The unique identifier of the folder to rename.
     * @param newName The new name for the folder.
     * @param newLastModified The new last modified timestamp for the folder.
     */
    suspend fun uploadFolder(userId: String, folders: List<FolderEntity>) {
        val foldersRef = firestore.collection("users")
            .document(userId)
            .collection("folders")

        folders.forEach { folder ->
            foldersRef.document(folder.folderUuid).set(folder).await()
        }
    }

    suspend fun getAllFoldersFromCloud(userId: String): List<FolderEntity> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("folders")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(FolderEntity::class.java) }
    }

    suspend fun deleteFolder(userId: String, folderUuid: String) {
        firestore.collection("users")
            .document(userId)
            .collection("folders")
            .document(folderUuid)
            .delete()
            .await()
    }

    suspend fun uploadOneFolder(userId: String, folder: FolderEntity) {
        firestore.collection("users")
            .document(userId)
            .collection("folders")
            .document(folder.folderUuid)
            .set(folder)
            .await()
    }

    suspend fun renameFolder(
        userId: String,
        folderUuid: String,
        newName: String,
        newLastModified: Long
    ) {

        val changhes = mapOf(
            "folderName" to newName,
            "lastModified" to newLastModified
        )

        firestore.collection("users")
            .document(userId)
            .collection("folders")
            .document(folderUuid)
            .update(changhes)
            .await()
    }
}