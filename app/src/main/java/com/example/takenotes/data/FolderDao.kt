package com.example.takenotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.takenotes.data.model.api.Folder
import com.example.takenotes.data.model.api.Note
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(note: Folder): Single<Long>

    @Query("SELECT * FROM folder_table")
    fun getAllFolders(): Flowable<List<Folder>>

    @Query("SELECT * FROM folder_table WHERE id = :folderId")
    fun getFolder(folderId: Long): Single<Folder>

    @Query("DELETE FROM folder_table WHERE id = :folderId")
    fun deleteFolder(folderId: Long): Completable

    @Query("DELETE FROM folder_table WHERE id IN (:ids)")
    fun deleteFolders(ids: List<Long>): Completable
}