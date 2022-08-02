package com.example.keybindhelper.dao

import com.example.keybindhelper.dto.Project
import androidx.lifecycle.MutableLiveData
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dao.CurrentProjectManager

/**
 * Static class to hold current project info and manage operations
 */
object CurrentProjectManager {
    @JvmField
    var CurrentProject: Project? = null

    //   public static List<Group> Groups;
    var isProjectLoaded = MutableLiveData(false)

    /**
     * Loads most recent project from DB or creates new project if none exist
     */
    fun loadFirstProject() {
        val projects = DatabaseManager.orderedProjects
        val p: Project
        if (projects!!.isEmpty()) {
            p = Project()
            p.name.value = "Unnamed Project"
            p.id = DatabaseManager.db!!.insert(p)
            println("NEW PROJECT INSERTED: projectId" + p.id)
        } else {
            p = projects[0]!!
            println("A PROJECT ALREADY EXISTS projectId: " + p.id)
        }
        loadProject(p, true)
    }

    /**
     * Loads project from db
     * @param project project to load
     * @param updateDb whether to update db
     */
    fun loadProject(project: Project, updateDb: Boolean) {
        if (CurrentProject != null) {
            CurrentProject!!.UnloadProject()
        }
        CurrentProject = project
        project.updateLastAccessed()
        if (updateDb)
            DatabaseManager.db!!.update(project)
        CurrentProject!!.initProject()
        isProjectLoaded.value = true
    }
}