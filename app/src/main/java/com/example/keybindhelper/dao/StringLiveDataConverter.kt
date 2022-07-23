package com.example.keybindhelper.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter

class StringLiveDataConverter {
    companion object    {
        @JvmStatic
        @TypeConverter
        fun toLiveData(s: String): MutableLiveData<String> {
            return MutableLiveData(s)
        }

        @JvmStatic
        @TypeConverter
        fun toString(s: MutableLiveData<String?>): String? {
            return s.value
        }
    }
}