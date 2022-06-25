package com.example.keybindhelper.dto

import androidx.room.*
import com.example.keybindhelper.Adapters.GroupAdapter
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.CurrentProjectManager.firstKeybindUnnamed
import com.example.keybindhelper.dao.CurrentProjectManager.getFirstGroupUnnamed
import com.example.keybindhelper.dao.DatabaseManager
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
    @ColumnInfo
    var name: String? = null

    @JvmField
    @ColumnInfo(name = "projectID")
    var projectID: Long = 0

    @JvmField
    @ColumnInfo(name = "index")
    var index = 0

    @Ignore
    var keybinds: MutableList<Keybind> = mutableListOf();


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
    constructor(name: String?, projectID: Long) {
        this.name = name
        this.projectID = projectID
    }

    /**
     * Creates a new keybind, adds it to the group and inserts it into the database
     */
    fun AddKeybind() {
        val kb = Keybind()
        kb.name = firstKeybindUnnamed
        keybinds.add(kb)
        kb.index = keybinds.size - 1
        kb.group = this
        kb.groupID = id
        DatabaseManager.db!!.insert(kb)
    }

    /**
     * Fetches keybinds from room database
     */
    fun getKeybinds() {
        keybinds = ArrayList()
        val kb = DatabaseManager.db!!.getGroupKeybinds(id)
        kb.sortWith { a: Keybind?, b: Keybind? -> a!!.index - b!!.index }
        keybinds = kb
        for (k in keybinds) {
            k.group = this
        }
    }

    /**
     * Removes the keybind from the groups keybind list and deletes the keybind from the database
     * @param k
     */
    fun deleteKeybind(k: Keybind) {
        DatabaseManager.db!!.delete(k)
        keybinds.minus(k)
        UpdateKeybindIndexes()
    }

    /**
     * Sets the keybinds index's to its current position in the groups keybind list
     */
    fun UpdateKeybindIndexes() {
        for (k in keybinds) {
            k.index = keybinds.indexOf(k)
            k.updateDB()
        }
    }

    /**
     * Adds the keybind to this group and removes attachments to old group
     * @param kb Keybind
     * @param insertToDB whether to insert it into database or update the database
     */
    fun AddKeybind(kb: Keybind?, insertToDB: Boolean) {
        if (kb!!.group != null && kb.group!!.keybinds!!.contains(kb)) {
            kb.group!!.keybinds.minus(kb)
            kb.group!!.UpdateKeybindIndexes()
        }
        kb.group = this
        kb.groupID = id
        kb.index = keybinds!!.size
        keybinds.plus(kb)
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
        val ret = Group(getFirstGroupUnnamed(
            name!!), projectID)
        CurrentProjectManager.Groups!!.add(ret)
        ret.index = CurrentProjectManager.Groups!!.size - 1
        ret.id = DatabaseManager.db!!.insert(ret)
        if (keybinds != null) {
            ret.keybinds = mutableListOf()
            for (k in keybinds) {
                ret.AddKeybind(k.Clone(true), true)
            }
        }
        return ret
    }

    override fun toString(): String {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projectID=" + projectID +
                ", index=" + index +
                ", keybinds=" + keybinds!!.size +
                '}'
    }

    /**
     * Removes group adapter that has been attched to the class
     * as well as all the keybinds adapters
     */
    fun unloadStoredViews() {
        currentAdapter = null
        if (keybinds == null) for (k in keybinds!!) k!!.viewHolder = null
    }

    /**
     * Move the keybind in the group
     * @param k Keybind that is being moved
     * @param Direction 1 for down, -1 for up
     */
    fun moveKeybindUpDown(k: Keybind?, Direction: Int) {
        val index = keybinds.indexOf(k)
        keybinds.minus(k)
        keybinds.add(index + Direction, k!!)
        UpdateKeybindIndexes()
    }
}