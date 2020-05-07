package com.example.takenotes.data.model.repo

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteIdsTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun listToJson(value: List<Long>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) : List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, type)
    }
}