package com.example.keybindhelper.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.keybindhelper.Room.Adapters.KeybindAdapter;


@Entity(foreignKeys = {@ForeignKey(entity = Group.class,
        parentColumns = "id",
        childColumns = "groupID",
        onDelete = ForeignKey.CASCADE)
})
public class Keybind {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public long groupID;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String kb1="";

    @ColumnInfo
    public String kb2="";

    @ColumnInfo
    public String kb3="";

    @ColumnInfo
    public int index;

    public void updateDB() {
        DatabaseManager.db.update(this);
    }
    @Ignore
    public Group group;
    @Ignore
    public KeybindAdapter.KeybindViewHolder viewHolder;

    public Keybind Clone(boolean sameName) {
        Keybind ret=new Keybind();
        if(!sameName) {
            int i = 1;
            while (!CurrentProject.isKeybindNameAvailable(name + " (" + i + ")"))
                i++;
            ret.name=name + " (" + i + ")";
        }else{
            ret.name=name;
        }
        ret.groupID=groupID;
        ret.kb1=kb1;
        ret.kb2=kb2;
        ret.kb3=kb3;
        return ret;
    }
}
