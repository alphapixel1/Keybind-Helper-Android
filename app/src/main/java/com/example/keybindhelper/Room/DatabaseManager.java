package com.example.keybindhelper.Room;

import android.content.Context;

import androidx.room.Room;

import java.util.Collections;
import java.util.List;

public class DatabaseManager {
    public static ProjectDao db;
    public static void init(Context c){
        AppDatabase db = Room.databaseBuilder(c, AppDatabase.class, "ProjectDB").allowMainThreadQueries().build();
        DatabaseManager.db =db.projectDao();

    }
    public static List<Project> getOrderedProjects(){
        List<Project> projects= db.getProjects();
        Collections.sort(projects,(a, b)-> b.lastAccessed.compareTo(a.lastAccessed));
        return projects;
    }
    public static boolean isProjectNameAvailable(String name){
        for (Project p : db.getProjects()){
            if(p.name.equals(name))
                return false;
        }
        return true;
    }
}
