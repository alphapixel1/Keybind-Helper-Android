package com.example.keybindhelper.dao

import androidx.room.Database
import com.example.keybindhelper.dto.Project
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dto.ThemeDTO
import androidx.room.RoomDatabase
import com.example.keybindhelper.dao.ProjectDao
import com.example.keybindhelper.dto.Group

@Database(entities = [Project::class, Group::class, Keybind::class, ThemeDTO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao?
}