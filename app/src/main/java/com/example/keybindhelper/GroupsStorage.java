package com.example.keybindhelper;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Objects;

public class GroupsStorage {
    public static ArrayList<KeybindGroup> Groups=new ArrayList<>();

    public static Boolean isGroupNameAvailable(String name){
        for (KeybindGroup g: Groups) {
            if(Objects.equals(g.getName(),name))
                return false;
        }
        return true;
    }

    public static String GetFirstGroupUnnamed(String startingPoint){
        if(isGroupNameAvailable(startingPoint)){
            return startingPoint;
        }
        int i=1;
        while (!isGroupNameAvailable(startingPoint+" "+i))
            i++;
        return startingPoint+" "+i;
    }
    public static Boolean isKeybindNameAvailable(String name){
        for (KeybindGroup g: Groups) {
            for (Keybind kb: g.Keybinds) {
                if(kb.name.equals(name))
                    return false;
            }
        }
        return true;
    }
    public static String GetFirstKeybindUnnamed(){
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
}
