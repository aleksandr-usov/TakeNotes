package com.example.takenotes.data.model.repo

import com.example.takenotes.data.NoteDao
import com.example.takenotes.data.model.api.Note

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes() = noteDao.getAllNotes()

    fun insertNote(note: Note) = noteDao.insertNote(note)

    fun deleteNote(noteId: Long) = noteDao.deleteNote(noteId)

    fun deleteNotes(ids: List<Long>) = noteDao.deleteNotes(ids)
}