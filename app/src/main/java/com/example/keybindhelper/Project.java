package com.example.keybindhelper;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Objects;
@Entity
public class Project {
    public static ArrayList<KeybindGroup> Groups=new ArrayList<>();
    @PrimaryKey
    public static int ProjectID;
    @ColumnInfo(name="Name")
    public static String ProjectName;
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

    public static int GetAvailableGroupID() {
        int i=0;
        while (!isIDAvailable(i)){
            i++;
        }
        return i;
    }
    private static Boolean isIDAvailable(int id){
        for (KeybindGroup g:Groups) {
            if(g.ID==id)
                return false;
        }
        return true;
    }
}
