package com.example.keybindhelper.dto

import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.keybindhelper.dao.*
import com.example.keybindhelper.dto.Group.Companion.fromJSONObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Entity
class Project {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JvmField
    @TypeConverters(StringLiveDataConverter::class)
    var name = MutableLiveData<String>()


    @JvmField
    @TypeConverters(DateConverter::class)
    var lastAccessed: Date? = null

    @Ignore
    var Groups: MutableList<Group> = mutableListOf()
    fun updateLastAccessed() {
        lastAccessed = Date()
    }

    /**
     * adds Groups from the database.
     */
    fun initProject() {
        Groups = DatabaseManager.getOrderedGroups(id)!!.map{it!!}.toMutableList()
        Groups.forEach { it.getKeybinds() }
    }

    /**
     * Removes all stored views
     */
    fun UnloadProject() {
        Groups.forEach { it.unloadStoredViews() }
    }

    /**
     * checks all keybinds in project if name has been taken
     * @param name
     * @return
     */
    fun isKeybindNameAvailable(name: String): Boolean {
        for (g in Groups) {
            for (kb in g.keybinds) {
                if (kb.name.value != null && kb.name.value == name) return false
            }
        }
        return true
    }

    /**
     * Adds (#) to end of name and increments till keybind name is available
     * @return name plus (#)
     */
    val firstKeybindUnnamed: String
        get() {
            val n = "Unnamed Keybind"
            if (isKeybindNameAvailable(n)) {
                return n
            }
            var i = 1
            while (!isKeybindNameAvailable("$n $i")) {
                i++
            }
            return "$n $i"
        }

    /**
     * Checks if current project's group name has already been taken
     * @param name
     * @return
     */
    fun isGroupNameAvailable(name: String?): Boolean {
        for (g in Groups) {
            if (g.name.value == name) return false
        }
        return true
    }

    /**
     * Adds (#) to end of name and increments till group name is available
     * @param startingPoint
     * @return
     */
    fun getFirstGroupUnnamed(startingPoint: String): String {
        if (isGroupNameAvailable(startingPoint)) {
            return startingPoint
        }
        var i = 1
        while (!isGroupNameAvailable("$startingPoint ($i)")) i++
        return "$startingPoint ($i)"
    }

    /**
     * Adds blank group to project and db
     * @return newly created group
     */
    fun AddGroup(): Group {
        val g = Group()
        Groups.add(g)
        g.projectID = id
        g.index = Groups.size - 1
        g.name.value = getFirstGroupUnnamed("Unnamed Group")
        println("CurrentProjectManager.AddGroup CurrentProjectID: $id")
        if(DatabaseManager.db!=null)
            g.id = DatabaseManager.db!!.insert(g)
        return g
    }

    /**
     * Moves the group up or down on the project group list
     * @param g Group to move
     * @param Direction Direction 1 for down, -1 for up
     */
    fun MoveGroupUpDown(g: Group, Direction: Int) {
        val index = Groups.indexOf(g)
        Groups.remove(g)
        Groups.add(index + Direction, g)
        updateGroupIndexes()
    }

    /**
     * Updates the db so that the groups order is saved
     */
    fun updateGroupIndexes() {
        var i = 0
        val groupsSize = Groups.size
        while (i < groupsSize) {
            val g = Groups[i]
            g.index = i
            DatabaseManager.db!!.update(g)
            i++
        }
    }

    /**
     * Deletes all groups
     */
    fun deleteAllGroups() {
        Groups.clear()
        DatabaseManager.db!!.deleteAllProjectsGroups(id)
    }

    fun getJSONObject(isCurrentProject: Boolean): JSONObject? {
        val ret = JSONObject()
        return try {
            ret.put("projectName", name.value)
            val groupsJSONArray = JSONArray()
            DatabaseManager.db!!.getProjectGroups(id)
            val groups: List<Group>?
            groups =
                if (isCurrentProject) CurrentProjectManager.CurrentProject!!.Groups else DatabaseManager.getOrderedGroups(
                    id)!!.map { it!! }
            for (g in groups) groupsJSONArray.put(g.getJSONObject(isCurrentProject))
            ret.put("groups", groupsJSONArray)
            ret
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJSONString(json: String?): Project {
            val p = JSONObject(json)
            val ret = Project()
            ret.updateLastAccessed()
            val newName = DatabaseManager.getFirstAvailableProjectName(p.getString("projectName"))
            ret.name.setValue(newName)
            val groups = p.getJSONArray("groups")
            ret.Groups = ArrayList()
            for (i in 0 until groups.length()) {
                ret.Groups.add(fromJSONObject(groups.getJSONObject(i)))
            }
            ret.id = DatabaseManager.db!!.insert(ret)
            for (gIndex in ret.Groups.indices) {
                val g = ret.Groups.get(gIndex)
                g.projectID = ret.id
                g.index = gIndex
                g.id = DatabaseManager.db!!.insert(g)
                for (kIndex in g.keybinds.indices) {
                    val k = g.keybinds[kIndex]
                    k.groupID = g.id
                    k.index = kIndex
                    k.id = DatabaseManager.db!!.insert(k)
                }
            }
            return ret
        }
    }
}