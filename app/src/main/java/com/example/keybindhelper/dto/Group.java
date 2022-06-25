package com.example.keybindhelper.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.keybindhelper.Adapters.GroupAdapter;
import com.example.keybindhelper.dao.CurrentProjectManager;
import com.example.keybindhelper.dao.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(foreignKeys = {@ForeignKey(entity = Project.class,
        parentColumns = "id",
        childColumns = "projectID",
        onDelete = ForeignKey.CASCADE)
})
public class Group {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo(name="projectID")
    public long projectID;

    @ColumnInfo(name="index")
    public int index;

    @Ignore
    public List<Keybind> keybinds=new ArrayList<>();
    @Ignore
    public GroupAdapter currentAdapter;

    /**
     * Blank For Room
     */
    public Group(){}

    /**
     * Initializes the group with a name and projectID
     * @param name
     * @param projectID
     */
    public Group(String name, long projectID){
        this.name=name;
        this.projectID=projectID;
    }

    /**
     * Creates a new keybind, adds it to the group and inserts it into the database
     */
    public void AddKeybind() {
        Keybind kb=new Keybind();
        kb.name= CurrentProjectManager.getFirstKeybindUnnamed();
        keybinds.add(kb);
        kb.index=keybinds.size()-1;
        kb.group=this;
        kb.groupID=id;
        DatabaseManager.db.insert(kb);
    }

    /**
     * Fetches keybinds from room database
     */
    public void getKeybinds() {

        this.keybinds=new ArrayList<>();
        List<Keybind> kb=DatabaseManager.db.getGroupKeybinds(id);
        Collections.sort(kb,(a,b)->a.index-b.index);
        keybinds=kb;
        for (Keybind k:keybinds) {
            k.group=this;
        }
    }

    /**
     * Removes the keybind from the groups keybind list and deletes the keybind from the database
     * @param k
     */
    public void deleteKeybind(Keybind k) {
        DatabaseManager.db.delete(k);
        keybinds.remove(k);
        UpdateKeybindIndexes();
    }

    /**
     * Sets the keybinds index's to its current position in the groups keybind list
     */
    public void UpdateKeybindIndexes(){
        for (Keybind k: keybinds) {
            k.index=keybinds.indexOf(k);
            k.updateDB();
        }
    }

    /**
     * Adds the keybind to this group and removes attachments to old group
     * @param kb Keybind
     * @param insertToDB whether to insert it into database or update the database
     */
    public void AddKeybind(Keybind kb,boolean insertToDB) {
        if(kb.group!=null && kb.group.keybinds.contains(kb)){
            kb.group.keybinds.remove(kb);
            kb.group.UpdateKeybindIndexes();
        }
        kb.group=this;
        kb.groupID=id;
        kb.index=keybinds.size();
        keybinds.add(kb);
        if(insertToDB){
            kb.id=DatabaseManager.db.insert(kb);
        }else {
            kb.updateDB();
        }
    }

    /**
     * Clones the group and inserts it into the current project
     * @return the new group
     */
    public Group Clone() {
        Group ret = new Group(CurrentProjectManager.getFirstGroupUnnamed(name),projectID);
        CurrentProjectManager.Groups.add(ret);
        ret.index = CurrentProjectManager.Groups.size() - 1;
        ret.id = DatabaseManager.db.insert(ret);
        if (keybinds != null) {
            ret.keybinds = new ArrayList<>();
            for (Keybind k : keybinds) {
                ret.AddKeybind(k.Clone(true), true);
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projectID=" + projectID +
                ", index=" + index +
                ", keybinds=" + keybinds.size() +
                '}';
    }

    /**
     * Removes group adapter that has been attched to the class
     * as well as all the keybinds adapters
     */
    public void unloadStoredViews() {
        currentAdapter=null;
        if(keybinds==null)
            for (Keybind k : keybinds)
                k.viewHolder=null;
    }

    /**
     * Move the keybind in the group
     * @param k Keybind that is being moved
     * @param Direction 1 for down, -1 for up
     */
    public void moveKeybindUpDown(Keybind k, int Direction){
        int index=keybinds.indexOf(k);
        keybinds.remove(k);
        keybinds.add(index+Direction,k);
        UpdateKeybindIndexes();
    }

    public JSONObject getJSONObject(boolean isCurrentProject) throws JSONException {
        JSONObject ret =new JSONObject();
        ret.put("groupName",name);
        JSONArray keybindsJSONArray=new JSONArray();
        List<Keybind> kbs;
        if(isCurrentProject){
            kbs=keybinds;
        }else{
            kbs=DatabaseManager.getOrderedKeybinds(id);
        }
        for (Keybind k : kbs){
            keybindsJSONArray.put(k.getJSONObject());
        }
        ret.put("keybinds",keybindsJSONArray);
        return ret;
    }
}
