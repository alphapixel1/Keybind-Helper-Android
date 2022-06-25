package com.example.keybindhelper.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.keybindhelper.dao.CurrentProjectManager;
import com.example.keybindhelper.dao.DatabaseManager;
import com.example.keybindhelper.dao.DateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@Entity
public class Project {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    @TypeConverters({DateConverter.class})
    public Date lastAccessed;

    public void updateLastAccessed(){
        lastAccessed=new Date();
    }

    public JSONObject getJSONObject(boolean isCurrentProject){
        JSONObject ret=new JSONObject();
        try {
            ret.put("projectName",name);
            JSONArray groupsJSONArray=new JSONArray();
            DatabaseManager.db.getProjectGroups(id);
            List<Group> groups;
            if(isCurrentProject)
                groups= CurrentProjectManager.Groups;
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
