package com.example.keybindhelper.dao;

import androidx.lifecycle.MutableLiveData;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class StringLiveDataConverter {
    @TypeConverter
    public static MutableLiveData<String> toLiveData(String s){
        return new MutableLiveData<>(s);
    }

    @TypeConverter
    public static String toString(MutableLiveData<String> s){
        return s.getValue();
    }
}
