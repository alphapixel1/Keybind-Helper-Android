package com.example.keybindhelper.dao;

import androidx.room.TypeConverter;
import java.util.*;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp){
        return (timestamp == null)? null : new Date(timestamp);
    }
    @TypeConverter
    public static Long toTimestamp(Date date){
        return date.getTime();
    }
}
