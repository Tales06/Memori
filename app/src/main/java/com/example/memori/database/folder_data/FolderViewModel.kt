package com.example.memori.database.folder_data

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.preference.PinPreferences
import com.example.memori.preference.PinPreferences.getPinHash
import com.example.memori.preference.PinPreferences.savePinHash
import com.example.memori.sync.FirestoreFolderRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderViewModel(private val repository: FolderRepository) : ViewModel() {

    private val userId = Firebase.auth.currentUser?.uid
    private val repoFireStore = FirestoreFolderRepository()


    val allFolders: StateFlow<List<FolderEntity>> = repository.allFolders
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createFolder(folderName: String, context: Context, isProtected: Boolean = false) =
        viewModelScope.launch {

            val userId = Firebase.auth.currentUser?.uid


            val folder = FolderEntity(
                folderName = folderName,
                userId = userId,
                encryptedPin = context.getPinHash() ?: "",
                isProtected = isProtected,
            )

            val existing = repository.getFolderByUuid(folder.folderUuid).firstOrNull()
            if (existing == null) {
                repository.insert(folder)
            } else {
                // If the folder already exists, you might want to update it instead
                repository.insert(folder)
            }


            if (userId != null) {
                viewModelScope.launch {
                    repoFireStore.uploadOneFolder(userId, folder)
                }
            }


        }

    fun renameFolder(folderUuid: String, newName: String) = viewModelScope.launch {

        val newLastModified = System.currentTimeMillis()
        repository.updateFolderName(folderUuid, newName, newLastModified)
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            repoFireStore.renameFolder(userId, folderUuid, newName, newLastModified)
        }

    }

    fun getFolderByUuid(folderUuid: String): Flow<FolderEntity> {
        return repository.getFolderByUuid(folderUuid)
    }


    fun deleteFolder(folderId: Int, folderUuid: String) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
            val userId = Firebase.auth.currentUser?.uid
            if (userId != null) {
                repoFireStore.deleteFolder(userId, folderUuid)
            }
        }
    }

    fun syncAllFolders(userId: String, context: Context) = viewModelScope.launch {

        val localFolders = repository.allFolders.first()
        repoFireStore.uploadFolder(userId, localFolders)

        val cloudFolders = repoFireStore.getAllFoldersFromCloud(userId)

        cloudFolders.forEach { folder ->
            val localDb = repository.getFolderByUuid(folder.folderUuid).firstOrNull()
            if (localDb == null) {
                repository.insert(folder)
                context.savePinHash(folder.encryptedPin)
            } else if (folder.lastModified > localDb.lastModified) {
                context.savePinHash(folder.encryptedPin)
                repository.insert(folder)
            }
        }

    }


}