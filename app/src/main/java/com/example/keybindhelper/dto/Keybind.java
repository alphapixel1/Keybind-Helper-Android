package com.example.keybindhelper.dto;

import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.keybindhelper.RecyclerViewAdapters.KeybindAdapter;
import com.example.keybindhelper.dao.CurrentProjectManager;
import com.example.keybindhelper.dao.DatabaseManager;
import com.example.keybindhelper.dao.DateConverter;
import com.example.keybindhelper.dao.StringLiveDataConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@Entity(foreignKeys = {@ForeignKey(entity = Group.class,
        parentColumns = "id",
        childColumns = "groupID",
        onDelete = ForeignKey.CASCADE)
})
public class Keybind{
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public long groupID;
    @TypeConverters({StringLiveDataConverter.class})
    public MutableLiveData<String>
            name=new MutableLiveData<>(""),
            kb1=new MutableLiveData<>(""),
            kb2=new MutableLiveData<>(""),
            kb3=new MutableLiveData<>("");
   /* @ColumnInfo
    public String name,kb1="",kb2="",kb3="";*/

    @ColumnInfo
    public int index;

    /**
     * Blank for Room
     */
    public Keybind(){}

    /**
     * Used Exclusively for cloning keybinds
     * @param groupID
     * @param name
     * @param kb1
     * @param kb2
     * @param kb3
     */
    public Keybind(long groupID,String name,String kb1,String kb2,String kb3){
        this.groupID=groupID;
        this.name.setValue(name);
        this.kb1.setValue(kb1);
        this.kb2.setValue(kb2);
        this.kb3.setValue(kb3);
    }


    /**
     * Tells database manager to update the keybind row
     */
    public void updateDB() {
        DatabaseManager.db.update(this);
    }
    @Ignore
    public Group group;
    @Ignore
    public KeybindAdapter.KeybindViewHolder viewHolder;

    /**
     * Clones the keybind
     * @param sameName Should name be autogenerated and unique
     * @return cloned keybind
     */
    public Keybind Clone(boolean sameName) {

        String newName=name.getValue();
        if(!sameName) {
            int i = 1;
            while (!CurrentProjectManager.CurrentProject.isKeybindNameAvailable(name.getValue() + " (" + i + ")"))
                i++;
            newName=name.getValue() + " (" + i + ")";
        }
        Keybind ret=new Keybind(groupID,newName,kb1.getValue(),kb2.getValue(),kb3.getValue());
        return ret;
    }
    public JSONObject getJSONObject() throws JSONException {
        JSONObject ret=new JSONObject();
        ret.put("keybindName",name.getValue());
        JSONArray kbs=new JSONArray();
        if(kb1.getValue()!=null)
            kbs.put(kb1.getValue());
        if(kb2.getValue()!=null)
            kbs.put(kb2.getValue());
        if(kb3.getValue()!=null)
            kbs.put(kb3.getValue());
      /*  ret.put("kb1",kb1.getValue());
        ret.put("kb2",kb2.getValue());
        ret.put("kb3",kb3.getValue());*/
        ret.put("keybinds",kbs);
        return ret;
    }
    public static Keybind fromJSONObject(JSONObject jKb) throws JSONException {
        Keybind ret =new Keybind();
        ret.name.setValue(jKb.getString("keybindName"));
        JSONArray kbs=jKb.getJSONArray("keybinds");
        if(kbs.length()>0) {
            ret.kb1.setValue(kbs.getString(0));
            if(kbs.length()>1) {
                ret.kb2.setValue(kbs.getString(1));
                if(kbs.length()>2) {
                    ret.kb3.setValue(kbs.getString(2));
                }
            }
        }
        return ret;
    }

}
