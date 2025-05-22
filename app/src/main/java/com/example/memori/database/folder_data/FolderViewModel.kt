package com.example.memori.database.folder_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class FolderViewModel(private val repository: FolderRepository) : ViewModel()  {

    private val userId = Firebase.auth.currentUser?.uid
    private val repoFireStore = FirestoreFolderRepository()


    val allFolders: StateFlow<List<FolderEntity>> = repository.allFolders
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createFolder(folderName: String) {

        val userId = Firebase.auth.currentUser?.uid

        val folder = FolderEntity(
            folderName = folderName,
            userId = userId,
        )

        viewModelScope.launch {
            repository.insert(folder)
        }

        if (userId != null) {
            viewModelScope.launch {
                repoFireStore.uploadFolder(userId, listOf(folder))
            }
        }


    }

    fun getFolderByUuid(folderUuid: String): Flow<FolderEntity> {
        return repository.getFolderByUuid(folderUuid)
    }


    fun deleteFolder(folderId: Int) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
            val userId = Firebase.auth.currentUser?.uid
            if (userId != null) {
                repoFireStore.deleteFolder(userId, folderId)
            }
        }
    }

    fun syncAllFolders(userId: String){
        viewModelScope.launch {
            val localFolders = repository.allFolders.first()
            repoFireStore.uploadFolder(userId, localFolders)

            val cloudFolders = repoFireStore.getAllFoldersFromCloud(userId)

            cloudFolders.forEach { folder ->
                val localDb = repository.getFolderByUuid(folder.folderUuid).firstOrNull()
                if(localDb == null || folder.lastModified > localDb.lastModified){
                    repository.insert(folder)
                } else {
                    repository.update(folder)
                }
            }
        }
    }



}