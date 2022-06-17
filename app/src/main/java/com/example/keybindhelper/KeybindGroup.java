package com.example.keybindhelper;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
@Entity
public class KeybindGroup {
    public ArrayList<Keybind> Keybinds=new ArrayList<>();
    private String Name;
    private  Context context;
    @PrimaryKey
    public int ID;
    public GroupViewModel model;
    @ColumnInfo(name = "ProjectID")
    public int GetProjectID(){
        return GroupsStorage.ProjectID;
    }
    @ColumnInfo(name="Index")
    public int GetIndex(){return GroupsStorage.Groups.indexOf(this);}
    
    public KeybindGroup(Context context){
        this.context=context;
        SetName(GroupsStorage.GetFirstGroupUnnamed("Unnamed Group"));
        ID=GroupsStorage.GetAvailableGroupID();
        GroupsStorage.Groups.add(this);
        model =new GroupViewModel(context,this);
    }
    public void SetName(String name){
        Name=name;
        if(model !=null)
            model.UpdateName(name);
    }
    @ColumnInfo(name = "name")
    public String getName(){
        return Name;
    }
    public Keybind AddKeybind(){
        Keybind ret=new Keybind(context,this);
        this.Keybinds.add(ret);
        ret.group=this;
        model.AddKeybind(ret);
        return ret;
    }
    public Keybind AddKeybind(Keybind kb){
        if(kb.group!=null)
            kb.group.Keybinds.remove(kb);
        if(kb.model.view.getParent()!=null)
            ((LinearLayout)kb.model.view.getParent()).removeView(kb.model.view);
        this.Keybinds.add(kb);
        kb.group=this;
        model.AddKeybind(kb);
        return kb;
    }

    public View RebuildView(){
        model =new GroupViewModel(context,this);
        for (Keybind kb: Keybinds) {
            kb.RebuildView();
            model.AddKeybind(kb);
        }
        return model.view;
    }


    public KeybindGroup Clone() {
        KeybindGroup g=new KeybindGroup(context);
        for (Keybind kb:Keybinds) {
            g.AddKeybind(kb.Clone(true));
        }
        return g;
    }
}

