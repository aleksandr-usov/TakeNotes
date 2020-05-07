package com.example.takenotes.data.model.repo

import com.example.takenotes.data.FolderDao
import com.example.takenotes.data.model.api.Folder

class FolderRepository(private val folderDao: FolderDao) {

    fun getAllFolders() = folderDao.getAllFolders()

    fun insertFolder(folder: Folder) = folderDao.insertFolder(folder)

    fun deleteFolder(folderId: Long) = folderDao.deleteFolder(folderId)

    fun deleteFolders(ids: List<Long>) = folderDao.deleteFolders(ids)
}