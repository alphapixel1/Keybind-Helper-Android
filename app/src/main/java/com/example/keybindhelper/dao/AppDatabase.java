package com.example.keybindhelper.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;
import com.example.keybindhelper.dto.ThemeDTO;

@Database(entities = {Project.class, Group.class, Keybind.class, ThemeDTO.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectDao projectDao();
}
