package com.example.takenotes.data.model.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder_table")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var noteIds: List<Long>,
    val dateCreated: String,
    val dateModified: String,
    val title: String
)