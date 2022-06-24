package com.example.keybindhelper.Room;


import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CurrentProject {
    public static Project CurrentProject;
    public static List<Group> Groups;
    public static MutableLiveData<Boolean> isProjectLoaded=new MutableLiveData<>(false);
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
  /*  public static void newProject(){
        List<Project> p= DatabaseManager.db.getProjects();
        String s="Unnamed Project";
        if(!isProjectNameAvailable(p,s)){
            int i = 1;
            while (!isProjectNameAvailable(p,s+" ("+i+")"))
                i++;
            s=s+" ("+i+")";
        }
        Project np=new Project();
        np.name=s;
        np.id= DatabaseManager.db.insert(np);
        loadProject(np,);
    }*/
    public static boolean isProjectNameAvailable(List<Project> projects,String name){
        for (Project p:projects) {
            if(p.name.equals(name))
                return false;
        }
        return true;
    }

    public static Boolean isGroupNameAvailable(String name){
        for (Group g: Groups) {
            if(Objects.equals(g.name,name))
                return false;
        }
        return true;
    }
    public static String getFirstGroupUnnamed(String startingPoint){
        if(isGroupNameAvailable(startingPoint)){
            return startingPoint;
        }
        int i=1;
        while (!isGroupNameAvailable(startingPoint+" ("+i+")"))
            i++;
        return startingPoint+" ("+i+")";
    }
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

    public static Group AddGroup(){
        Group g=new Group();
        Groups.add(g);
        g.projectID=CurrentProject.id;
        g.index=Groups.size()-1;
        g.name= getFirstGroupUnnamed("Unnamed Group");
        g.id= DatabaseManager.db.insert(g);
        return g;
    }

    public static void updateGroupIndexes() {
        for (int i = 0, groupsSize = Groups.size(); i < groupsSize; i++) {
            Group g=Groups.get(i);
            g.index=i;
            DatabaseManager.db.update(g);
        }
    }
    public static void deleteAllGroups(){
        Groups.clear();
        DatabaseManager.db.deleteAllProjectsGroups(CurrentProject.id);
    }
}
