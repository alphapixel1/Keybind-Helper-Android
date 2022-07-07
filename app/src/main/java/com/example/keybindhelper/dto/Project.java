package com.example.keybindhelper.dto;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.keybindhelper.dao.CurrentProjectManager;
import com.example.keybindhelper.dao.DatabaseManager;
import com.example.keybindhelper.dao.DateConverter;
import com.example.keybindhelper.dao.StringLiveDataConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Project{
    @PrimaryKey(autoGenerate = true)
    public long id;
    @TypeConverters({StringLiveDataConverter.class})
    public MutableLiveData<String> name;

    @TypeConverters({DateConverter.class})
    public Date lastAccessed;
    @Ignore
    public List<Group> Groups;


    public void updateLastAccessed(){
        lastAccessed=new Date();
    }

    /**
     * adds Groups from the database.
     */
    public void initProject(){
        Groups=DatabaseManager.getOrderedGroups(id);
        for (Group g:Groups) {
            g.getKeybinds();
        }
    }

    /**
     * Removes all stored views
     */
    public void UnloadProject(){
        for (Group g : Groups)
            g.unloadStoredViews();
    }
    /**
     * checks all keybinds in project if name has been taken
     * @param name
     * @return
     */
    public Boolean isKeybindNameAvailable(String name){
        for (Group g: Groups) {
            if(g.keybinds!=null) {
                for (Keybind kb : g.keybinds) {

                    if (kb.name.getValue()!=null && kb.name.getValue().equals(name))
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
    public String getFirstKeybindUnnamed(){
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
     * Checks if current project's group name has already been taken
     * @param name
     * @return
     */
    public Boolean isGroupNameAvailable(String name){
        for (Group g: Groups) {
            if(Objects.equals(g.name.getValue(),name))
                return false;
        }
        return true;
    }

    /**
     * Adds (#) to end of name and increments till group name is available
     * @param startingPoint
     * @return
     */
    public String getFirstGroupUnnamed(String startingPoint){
        if(isGroupNameAvailable(startingPoint)){
            return startingPoint;
        }
        int i=1;
        while (!isGroupNameAvailable(startingPoint+" ("+i+")"))
            i++;
        return startingPoint+" ("+i+")";
    }


    /**
     * Adds blank group to project and db
     * @return newly created group
     */
    public Group AddGroup(){
        Group g=new Group();
        Groups.add(g);
        g.projectID=id;
        g.index=Groups.size()-1;
        g.name.setValue(getFirstGroupUnnamed("Unnamed Group"));
        System.out.println("CurrentProjectManager.AddGroup CurrentProjectID: "+id);
        g.id= DatabaseManager.db.insert(g);
        return g;
    }
    /**
     * Moves the group up or down on the project group list
     * @param g Group to move
     * @param Direction Direction 1 for down, -1 for up
     */
    public void MoveGroupUpDown(Group g, int Direction){
        int index=Groups.indexOf(g);
        Groups.remove(g);
        Groups.add(index +Direction, g);
        updateGroupIndexes();
    }
    /**
     * Updates the db so that the groups order is saved
     */
    public void updateGroupIndexes() {
        for (int i = 0, groupsSize = Groups.size(); i < groupsSize; i++) {
            Group g=Groups.get(i);
            g.index=i;
            DatabaseManager.db.update(g);
        }
    }
    /**
     * Deletes all groups
     */
    public void deleteAllGroups(){
        Groups.clear();
        DatabaseManager.db.deleteAllProjectsGroups(id);
    }


    public JSONObject getJSONObject(boolean isCurrentProject){
        JSONObject ret=new JSONObject();
        try {
            ret.put("projectName",name.getValue());
            JSONArray groupsJSONArray=new JSONArray();
            DatabaseManager.db.getProjectGroups(id);
            List<Group> groups;
            if(isCurrentProject)
                groups= CurrentProjectManager.CurrentProject.Groups;
            else
                groups = DatabaseManager.getOrderedGroups(id);

            for(Group g : groups)
                groupsJSONArray.put(g.getJSONObject(isCurrentProject));

            ret.put("groups",groupsJSONArray);
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
