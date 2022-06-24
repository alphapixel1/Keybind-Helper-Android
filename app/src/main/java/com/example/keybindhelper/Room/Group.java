package com.example.keybindhelper.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.keybindhelper.Room.Adapters.GroupAdapter;

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

    public void AddKeybind() {
        Keybind kb=new Keybind();
        kb.name= CurrentProject.getFirstKeybindUnnamed();
        keybinds.add(kb);
        kb.index=keybinds.size()-1;
        kb.group=this;
        kb.groupID=id;
        DatabaseManager.db.insert(kb);
    }

    public void getKeybinds() {

        this.keybinds=new ArrayList<>();
        List<Keybind> kb=DatabaseManager.db.getGroupKeybinds(id);
        //System.out.println("GROUP.GETKEYBINDS: "+kb.size());
        Collections.sort(kb,(a,b)->a.index-b.index);
        keybinds=kb;
        for (Keybind k:keybinds) {
            k.group=this;
        }
    }

    public void deleteKeybind(Keybind k) {
        DatabaseManager.db.delete(k);
        keybinds.remove(k);
        UpdateKeybindIndexes();
    }
    public void UpdateKeybindIndexes(){
        for (Keybind k: keybinds) {
            k.index=keybinds.indexOf(k);
            k.updateDB();
        }
    }

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
    public Group Clone() {
        Group ret = new Group();
        ret.name = CurrentProject.getFirstGroupUnnamed(name);
        CurrentProject.Groups.add(ret);
        ret.projectID = projectID;
        ret.index = CurrentProject.Groups.size() - 1;
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

    public void unloadStoredViews() {
        currentAdapter=null;
        if(keybinds==null)
            for (Keybind k : keybinds)
                k.viewHolder=null;
    }

    public void moveKeybindUpDown(Keybind k, int Direction){
        int index=keybinds.indexOf(k);
        keybinds.remove(k);
        keybinds.add(index+Direction,k);
        UpdateKeybindIndexes();
    }
}
