package com.example.keybindhelper.dao

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    companion object    {
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

}