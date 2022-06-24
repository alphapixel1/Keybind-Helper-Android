package com.example.keybindhelper.dao;


import androidx.lifecycle.MutableLiveData;

import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Static class to hold current project info and manage operations
 */
public class CurrentProjectManager {
    public static Project CurrentProject;
    public static List<Group> Groups;
    public static MutableLiveData<Boolean> isProjectLoaded=new MutableLiveData<>(false);

    /**
     * Loads most recent project from DB or creates new project if none exist
     */
    public static void loadFirstProject(){
        List<Project> projects =DatabaseManager.getOrderedProjects();
        Project p;
        if(projects.isEmpty()){
            p=new Project();
            p.name="Unnamed Project";
            p.id= DatabaseManager.db.insert(p);
            System.out.println("NEW PROJECT INSERTED: "+p.id);
        }else{
            p=projects.get(0);
            System.out.println("A PROJECT ALREADY EXISTS: "+p.id);
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
            for (Group g :Groups)
                g.unloadStoredViews();
        }

        CurrentProject=project;
        project.updateLastAccessed();
        if(updateDb)
            DatabaseManager.db.update(project);
        Groups= DatabaseManager.db.getProjectGroups(project.id);
        Collections.sort(Groups,(a,b)->a.index-b.index);
        System.out.println("How many groups does this project have: "+Groups.size());
        for (Group g : Groups) {
            g.getKeybinds();
            System.out.println(g);
        }
        isProjectLoaded.setValue(true);
    }

    /**
     * Checks if name has already been taken
     * @param projects projects to check
     * @param name
     * @return if name is available
     */
    public static boolean isProjectNameAvailable(List<Project> projects,String name){
        for (Project p:projects) {
            if(p.name.equals(name))
                return false;
        }
        return true;
    }

    /**
     * Checks if current project's group name has already been taken
     * @param name
     * @return
     */
    public static Boolean isGroupNameAvailable(String name){
        for (Group g: Groups) {
            if(Objects.equals(g.name,name))
                return false;
        }
        return true;
    }

    /**
     * Adds (#) to end of name and increments till group name is available
     * @param startingPoint
     * @return
     */
    public static String getFirstGroupUnnamed(String startingPoint){
        if(isGroupNameAvailable(startingPoint)){
            return startingPoint;
        }
        int i=1;
        while (!isGroupNameAvailable(startingPoint+" ("+i+")"))
            i++;
        return startingPoint+" ("+i+")";
    }

    /**
     * checks all keybinds in project if name has been taken
     * @param name
     * @return
     */
    public static Boolean isKeybindNameAvailable(String name){
        for (Group g: Groups) {
            if(g.keybinds!=null) {
                for (Keybind kb : g.keybinds) {

                    if (kb.name!=null && kb.name.equals(name))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds (#) to end of name and increments till keybind name is available
     * @return name plus (#)
     */
    public static String getFirstKeybindUnnamed(){
        String n="Unnamed Keybind";
        if(isKeybindNameAvailable(n)){
            return n;
        }
        int i=1;
        while (!isKeybindNameAvailable(n+" "+i)){
            i++;
        }
        return n+" "+i;
    }

    /**
     * Adds blank group to project and db
     * @return newly created group
     */
    public static Group AddGroup(){
        Group g=new Group();
        Groups.add(g);
        g.projectID=CurrentProject.id;
        g.index=Groups.size()-1;
        g.name= getFirstGroupUnnamed("Unnamed Group");
        System.out.println("CurrentProjectManager.AddGroup CurrentProjectID: "+CurrentProject.id);
        g.id= DatabaseManager.db.insert(g);
        return g;
    }

    /**
     * Moves the group up or down on the project group list
     * @param g Group to move
     * @param Direction Direction 1 for down, -1 for up
     */
    public static void MoveGroupUpDown(Group g, int Direction){
        int index=Groups.indexOf(g);
        Groups.remove(g);
        Groups.add(index +Direction, g);
        updateGroupIndexes();
    }

    /**
     * Updates the db so that the groups order is saved
     */
    public static void updateGroupIndexes() {
        for (int i = 0, groupsSize = Groups.size(); i < groupsSize; i++) {
            Group g=Groups.get(i);
            g.index=i;
            DatabaseManager.db.update(g);
        }
    }

    /**
     * Deletes all groups
     */
    public static void deleteAllGroups(){
        Groups.clear();
        DatabaseManager.db.deleteAllProjectsGroups(CurrentProject.id);
    }
}
