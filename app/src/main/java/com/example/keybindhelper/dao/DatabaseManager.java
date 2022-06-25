package com.example.keybindhelper.dao;

import android.content.Context;

import androidx.room.Room;

import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;

import java.util.Collections;
import java.util.List;

/**
 * Static Database holder so I can access db anywhere in the project
 */
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
    public static List<Group> getOrderedGroups(long projectId){
        List<Group> Groups=db.getProjectGroups(projectId);
        Collections.sort(Groups,(a,b)->a.index-b.index);
        return Groups;
    }

    public static List<Keybind> getOrderedKeybinds(long id) {
        List<Keybind> keybinds=db.getGroupKeybinds(id);
        Collections.sort(keybinds,(a,b)->a.index-b.index);
        return keybinds;
    }
}
