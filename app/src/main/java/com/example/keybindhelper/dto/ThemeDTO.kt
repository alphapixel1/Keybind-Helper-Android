package com.example.keybindhelper.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Storing 1 row of theme data
 */
@Entity
class ThemeDTO {
    @PrimaryKey
    var primaryKey:Long=0;
    var ThemeName:String="Default";
}