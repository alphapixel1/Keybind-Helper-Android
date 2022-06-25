package com.example.keybindhelper.dao

import android.content.Context
import androidx.room.Room
import com.example.keybindhelper.dto.Project
import java.util.*

/**
 * Static Database holder so I can access db anywhere in the project
 */
object DatabaseManager {
    @JvmField
    var db: ProjectDao? = null
    fun init(c: Context?) {
        val db =
            Room.databaseBuilder(c!!, AppDatabase::class.java, "ProjectDB").allowMainThreadQueries()
                .build()
        DatabaseManager.db = db.projectDao()
    }

    val orderedProjects: MutableList<Project>
        get() {
            val projects = db!!.projects
            projects.sortWith { a: Project?, b: Project? ->
                b!!.lastAccessed!!.compareTo(a!!.lastAccessed)
            }
            return projects
        }

    @JvmStatic
    fun isProjectNameAvailable(name: String): Boolean {
        for (p in db!!.projects) {
            if (p!!.name == name) return false
        }
        return true
    }
}