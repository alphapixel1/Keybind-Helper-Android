package com.example.keybindhelper.dao

import com.example.keybindhelper.dto.Project
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dao.ProjectDao
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.KeybindStorage
import java.util.*

object DateConverter {
    @JvmStatic
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @JvmStatic
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}