package com.aiagent.data.db

import androidx.room.TypeConverter
import com.aiagent.data.model.GeneratedFile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromGeneratedFileList(files: List<GeneratedFile>): String {
        return gson.toJson(files)
    }
    
    @TypeConverter
    fun toGeneratedFileList(json: String): List<GeneratedFile> {
        val type = object : TypeToken<List<GeneratedFile>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }
    
    @TypeConverter
    fun toStringList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
