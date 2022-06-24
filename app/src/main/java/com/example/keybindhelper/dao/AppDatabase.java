package com.example.keybindhelper.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;

@Database(entities = {Project.class, Group.class, Keybind.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectDao projectDao();
}
