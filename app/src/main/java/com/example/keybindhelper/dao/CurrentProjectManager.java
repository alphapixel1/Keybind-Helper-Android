package com.example.keybindhelper.dao;


import androidx.lifecycle.MutableLiveData;

import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;

import java.util.List;
import java.util.Objects;

/**
 * Static class to hold current project info and manage operations
 */
public class CurrentProjectManager {
    public static Project CurrentProject;
 //   public static List<Group> Groups;
    public static MutableLiveData<Boolean> isProjectLoaded=new MutableLiveData<>(false);

    /**
     * Loads most recent project from DB or creates new project if none exist
     */
    public static void loadFirstProject(){
        List<Project> projects =DatabaseManager.getOrderedProjects();
        Project p;
        if(projects.isEmpty()){
            p=new Project();
            p.name.setValue("Unnamed Project");
            p.id= DatabaseManager.db.insert(p);
            System.out.println("NEW PROJECT INSERTED: projectId"+p.id);
        }else{
            p=projects.get(0);
            System.out.println("A PROJECT ALREADY EXISTS projectId: "+p.id);
        }
        loadProject(p,true);
    }

    /**
     * Loads project from db
     * @param project project to load
     * @param updateDb whether to update db
     */
    public static void loadProject(Project project,boolean updateDb){
        if(CurrentProject!=null){
            CurrentProject.UnloadProject();
        }
        CurrentProject=project;
        project.updateLastAccessed();

        if(updateDb)
            DatabaseManager.db.update(project);
       // CurrentProject.Groups= DatabaseManager.getOrderedGroups(project.id);
        //        System.out.println("How many groups does this project have: "+CurrentProject.Groups.size());
        CurrentProject.initProject();
        /*for (Group g : CurrentProject.Groups) {
            g.getKeybinds();
            System.out.println(g);
        }*/
        isProjectLoaded.setValue(true);
    }

}
