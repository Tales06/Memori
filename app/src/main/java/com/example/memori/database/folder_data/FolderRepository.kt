package com.example.memori.database.folder_data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class FolderRepository(private val foldersDao: FoldersDao) {

    val userId = Firebase.auth.currentUser?.uid

    val allFolders: Flow<List<FolderEntity>> = foldersDao.getFolders(userId)

    suspend fun insert(folder: FolderEntity) = foldersDao.insertFolder(folder)

    suspend fun update(folder: FolderEntity) = foldersDao.updateFolder(folder)

    suspend fun deleteFolder(folderId: Int) = foldersDao.deleteFolder(folderId = folderId)


    fun getFolderByUuid(folderUuid: String): Flow<FolderEntity> {
        return foldersDao.getFolderByUuid(folderUuid)
    }




}