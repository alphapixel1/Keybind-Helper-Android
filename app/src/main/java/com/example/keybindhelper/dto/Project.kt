package com.example.keybindhelper.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.keybindhelper.dao.DateConverter
import java.util.*

@Entity
class Project {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @JvmField
    var name: String? = null

    @JvmField
    @TypeConverters(DateConverter::class)
    var lastAccessed: Date? = null
    fun updateLastAccessed() {
        lastAccessed = Date()
    }
}