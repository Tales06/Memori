package com.example.memori.sync

import com.example.memori.database.folder_data.FolderEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreFolderRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

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