package com.example.keybindhelper.dto

import com.example.keybindhelper.dao.StringLiveDataConverter
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.keybindhelper.RecyclerViewAdapters.GroupAdapter
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray
import java.security.Key
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Project::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("projectID"),
    onDelete = ForeignKey.CASCADE)])
class Group {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JvmField
    @TypeConverters(StringLiveDataConverter::class)
    var name = MutableLiveData("")

    @JvmField
    @ColumnInfo(name = "projectID")
    var projectID: Long = 0

    @JvmField
    @ColumnInfo(name = "index")
    var index = 0

    @JvmField
    @Ignore
    var keybinds: MutableList<Keybind> = ArrayList()

    @Ignore
    var currentAdapter: GroupAdapter? = null

    /**
     * Blank For Room
     */
    constructor() {}

    /**
     * Initializes the group with a name and projectID
     * @param name
     * @param projectID
     */
    @Ignore
    constructor(name: String, projectID: Long) {
        this.name.value = name
        this.projectID = projectID
    }

    /**
     * Creates a new keybind, adds it to the group and inserts it into the database
     */
    fun addKeybind():Keybind {
        val kb = Keybind()
        if(CurrentProjectManager.CurrentProject!=null){
            kb.name.value = CurrentProjectManager.CurrentProject!!.firstKeybindUnnamed
        }
        keybinds.add(kb)
        kb.index = keybinds.size - 1
        kb.group = this
        kb.groupID = id

        DatabaseManager.db?.insert(kb)
        return kb;
    }

    /**
     * Fetches keybinds from room database
     */
    fun getKeybinds() {
        keybinds = ArrayList()
        val kb = DatabaseManager.db?.getGroupKeybinds(id)!!.map { it!! }.sortedBy { it.index }

        keybinds = kb.toMutableList()
        keybinds.forEach {
            it.group=this;
        }
    }

    /**
     * Removes the keybind from the groups keybind list and deletes the keybind from the database
     * @param k
     */
    fun deleteKeybind(k: Keybind) {

        keybinds.remove(k)
        if(DatabaseManager.db!=null) {
            DatabaseManager.db!!.delete(k)
            updateKeybinds()
        }

    }

    /**
     * Sets the keybinds index's to its current position in the groups keybind list
     */
    fun updateKeybinds() {
        for (i in 0 until keybinds.size){
            keybinds[i].index =i
            keybinds[i].updateDB()
        }

      /*  for (k in keybinds) {
            k.index = keybinds.indexOf(k)
            k.updateDB()
        }*/
    }

    /**
     * Adds the keybind to this group and removes attachments to old group
     * @param kb Keybind
     * @param insertToDB whether to insert it into database or update the database
     */
    fun addKeybind(kb: Keybind, insertToDB: Boolean) {
        if (kb.group != null && kb.group!!.keybinds.contains(kb)) {
            kb.group!!.keybinds.remove(kb)
            kb.group!!.updateKeybinds()
        }
        kb.group = this
        kb.groupID = id
        kb.index = keybinds.size
        keybinds.add(kb)
        if (insertToDB) {
            kb.id = DatabaseManager.db!!.insert(kb)
        } else {
            kb.updateDB()
        }
    }

    /**
     * Clones the group and inserts it into the current project
     * @return the new group
     */
    fun Clone(): Group {
        val ret = Group(
            CurrentProjectManager.CurrentProject!!.getFirstGroupUnnamed(name.value!!), projectID)
        CurrentProjectManager.CurrentProject!!.Groups.add(ret)
        ret.index = CurrentProjectManager.CurrentProject!!.Groups.size - 1
        ret.id = DatabaseManager.db!!.insert(ret)
        //if (keybinds != null) {
            ret.keybinds = ArrayList()
            for (k in keybinds) {
                ret.addKeybind(k.Clone(true), true)
            }
        //}
        return ret
    }

    override fun toString(): String {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projectID=" + projectID +
                ", index=" + index +
                ", keybinds=" + keybinds.size +
                '}'
    }

    /**
     * Removes group adapter that has been attched to the class
     * as well as all the keybinds adapters
     */
    fun unloadStoredViews() {
        currentAdapter = null
        keybinds.forEach { it.viewHolder=null }
        /*if (keybinds == null)
            for (k in keybinds!!) k.viewHolder = null*/
    }

    /**
     * Move the keybind in the group
     * @param k Keybind that is being moved
     * @param Direction 1 for down, -1 for up
     */
    fun moveKeybindUpDown(k: Keybind, Direction: Int) {
        val index = keybinds.indexOf(k)
        keybinds.remove(k)
        keybinds.add(index + Direction, k)
        updateKeybinds()
    }

    @Throws(JSONException::class)
    fun getJSONObject(isCurrentProject: Boolean): JSONObject {
        val ret = JSONObject()
        ret.put("groupName", name.value)
        val keybindsJSONArray = JSONArray()
        val kbs: List<Keybind>?
        kbs = if (isCurrentProject) {
            keybinds
        } else {
            DatabaseManager.getOrderedKeybinds(id)!!.map { it!! }
        }
        for (k in kbs) {
            keybindsJSONArray.put(k.JSONObject)
        }
        ret.put("keybinds", keybindsJSONArray)
        return ret
    }

    companion object {
        @JvmStatic
        @Throws(JSONException::class)
        fun fromJSONObject(jGroup: JSONObject): Group {
            val ret = Group()
            ret.name.value = jGroup.getString("groupName")
            val kbs = jGroup.getJSONArray("keybinds")
            for (i in 0 until kbs.length()) {
                ret.keybinds.add(Keybind.fromJSONObject(kbs.getJSONObject(i)))
            }
            return ret
        }
    }
}