package com.example.takenotes.data.model.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var folderId: String,
    val dateCreated: String,
    val dateModified: String,
    val title: String,
    var noteText: String
)