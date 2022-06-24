package com.example.keybindhelper.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Project.class,Group.class,Keybind.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectDao projectDao();
}
