package com.example.keybindhelper.dao

import androidx.lifecycle.MutableLiveData
import com.example.keybindhelper.dto.Group
import com.example.keybindhelper.dto.Project
import java.util.*

/**
 * Static class to hold current project info and manage operations
 */
object CurrentProjectManager {
    @JvmField
    var CurrentProject: Project? = null
    @JvmField
    var Groups: MutableList<Group>? = null
    var isProjectLoaded = MutableLiveData(false)

    /**
     * Loads most recent project from DB or creates new project if none exist
     */
    @JvmStatic
    fun loadFirstProject() {

        val projects = DatabaseManager.orderedProjects
        val p: Project?
        if (projects!!.isEmpty()) {
            p = Project()
            p.name = "Unnamed Project"
            p.id = DatabaseManager.db!!.insert(p)
            println("NEW PROJECT INSERTED: " + p.id)
        } else {
            p = projects!![0]
            println("A PROJECT ALREADY EXISTS: " + p!!.id)
        }
        loadProject(p, true)
    }

    /**
     * Loads project from db
     * @param project project to load
     * @param updateDb whether to update db
     */
    @JvmStatic
    fun loadProject(project: Project?, updateDb: Boolean) {
        if (CurrentProject != null) {
            for (g in Groups!!) g!!.unloadStoredViews()
        }
        CurrentProject = project
        project!!.updateLastAccessed()
        if (updateDb) DatabaseManager.db!!.update(project)
        Groups = DatabaseManager.db!!.getProjectGroups(project.id)
        Collections.sort(Groups) { a: Group?, b: Group? -> a!!.index - b!!.index }
        println("How many groups does this project have: " + Groups!!.size)
        for (g in Groups!!) {
            g!!.getKeybinds()
            println(g)
        }
        isProjectLoaded.value = true
    }

    /**
     * Checks if name has already been taken
     * @param projects projects to check
     * @param name
     * @return if name is available
     */
    @JvmStatic
    fun isProjectNameAvailable(projects: List<Project>, name: String): Boolean {
        for (p in projects) {
            if (p.name == name) return false
        }
        return true
    }

    /**
     * Checks if current project's group name has already been taken
     * @param name
     * @return
     */
    @JvmStatic
    fun isGroupNameAvailable(name: String?): Boolean {
        for (g in Groups!!) {
            if (g!!.name == name) return false
        }
        return true
    }

    /**
     * Adds (#) to end of name and increments till group name is available
     * @param startingPoint
     * @return
     */
    @JvmStatic
    fun getFirstGroupUnnamed(startingPoint: String): String {
        if (isGroupNameAvailable(startingPoint)) {
            return startingPoint
        }
        var i = 1
        while (!isGroupNameAvailable("$startingPoint ($i)")) i++
        return "$startingPoint ($i)"
    }

    /**
     * checks all keybinds in project if name has been taken
     * @param name
     * @return
     */
    @JvmStatic
    fun isKeybindNameAvailable(name: String): Boolean {
        for (g in Groups!!) {
            if (g!!.keybinds != null) {
                for (kb in g.keybinds!!) {
                    if (kb!!.name != null && kb.name == name) return false
                }
            }
        }
        return true
    }

    /**
     * Adds (#) to end of name and increments till keybind name is available
     * @return name plus (#)
     */
    @JvmStatic
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
     * Adds blank group to project and db
     * @return newly created group
     */
    fun AddGroup(): Group {
        val g = Group()
        Groups!!.add(g)
        g.projectID = CurrentProject!!.id
        g.index = Groups!!.size - 1
        g.name = getFirstGroupUnnamed("Unnamed Group")
        println("CurrentProjectManager.AddGroup CurrentProjectID: " + CurrentProject!!.id)
        g.id = DatabaseManager.db!!.insert(g)
        return g
    }

    /**
     * Moves the group up or down on the project group list
     * @param g Group to move
     * @param Direction Direction 1 for down, -1 for up
     */
    @JvmStatic
    fun MoveGroupUpDown(g: Group?, Direction: Int) {
        val index = Groups!!.indexOf(g)
        Groups!!.remove(g)
        Groups!!.add(index + Direction, g!!)
        updateGroupIndexes()
    }

    /**
     * Updates the db so that the groups order is saved
     */
    @JvmStatic
    fun updateGroupIndexes() {
        var i = 0
        val groupsSize = Groups!!.size
        while (i < groupsSize) {
            val g = Groups!![i]
            g.index = i
            DatabaseManager.db!!.update(g)
            i++
        }
    }

    /**
     * Deletes all groups
     */
    fun deleteAllGroups() {
        Groups!!.clear()
        DatabaseManager.db!!.deleteAllProjectsGroups(CurrentProject!!.id)
    }
}