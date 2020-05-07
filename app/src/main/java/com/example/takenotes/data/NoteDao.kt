package com.example.takenotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.takenotes.data.model.api.Note
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note): Single<Long>

    @Query("SELECT * FROM note_table")
    fun getAllNotes(): Flowable<List<Note>>

    @Query("SELECT * FROM note_table WHERE id = :noteId")
    fun getNote(noteId: Long): Single<Note>

    @Query("DELETE FROM note_table WHERE id = :noteId")
    fun deleteNote(noteId: Long): Completable

    @Query("DELETE FROM note_table WHERE id IN (:ids)")
    fun deleteNotes(ids: List<Long>): Completable
}