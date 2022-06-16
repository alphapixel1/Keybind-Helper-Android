package com.example.keybindhelper;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class KeybindGroup {
    public ArrayList<Keybind> Keybinds=new ArrayList<>();
    private String Name;
    private  Context context;
    public GroupViewModel model;

    public KeybindGroup(Context context){
        this.context=context;
        SetName(GroupsStorage.GetFirstGroupUnnamed("Unnamed Group"));
        GroupsStorage.Groups.add(this);
        model =new GroupViewModel(context,this);
    }
    public void SetName(String name){
        Name=name;
        if(model !=null)
            model.UpdateName(name);
    }
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

