package com.example.keybindhelper.dao

import android.content.Context
import androidx.room.Room
import com.example.keybindhelper.dto.Group
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dto.Project
import java.util.*

/**
 * Static Database holder so I can access db anywhere in the project
 */
object DatabaseManager {
    var db: ProjectDao?=null
    fun init(c: Context) {
        val db = Room.databaseBuilder(c, AppDatabase::class.java, "ProjectDB").allowMainThreadQueries().build()
        DatabaseManager.db = db.projectDao()!!
    }

    val orderedProjects: List<Project?>?
        get() {
            val projects = db!!.projects
            Collections.sort(projects) { a: Project?, b: Project? ->
                b!!.lastAccessed!!.compareTo(
                    a!!.lastAccessed)
            }
            return projects
        }

    fun isProjectNameAvailable(projects: List<Project?>?, name: String): Boolean {
        for (p in projects!!) {
            if (p!!.name.value == name) return false
        }
        return true
    }

    fun getFirstAvailableProjectName(baseName: String): String {
        val projects = db!!.projects
        if (isProjectNameAvailable(projects, baseName)) return baseName
        var i = 1
        while (!isProjectNameAvailable(projects, "$baseName ($i)")) {
            i++
        }
        return "$baseName ($i)"
    }

    fun getOrderedGroups(projectId: Long): List<Group?>? {
        val Groups = db!!.getProjectGroups(projectId)
        Collections.sort(Groups) { a: Group?, b: Group? -> a!!.index - b!!.index }
        return Groups
    }

    fun getOrderedKeybinds(id: Long): List<Keybind?>? {
        val keybinds = db!!.getGroupKeybinds(id)
        Collections.sort(keybinds) { a: Keybind?, b: Keybind? -> a!!.index - b!!.index }
        return keybinds
    }
}