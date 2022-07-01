package com.example.keybindhelper.dao

import com.example.keybindhelper.dto.Project
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dao.ProjectDao
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.KeybindStorage
import com.example.keybindhelper.dto.Group

@Dao
interface ProjectDao {
    //projects
    /**
     * Get all projects
     * @return returns all projects
     */
    @get:Query("Select * from Project")
    val projects: MutableList<Project>

    /**
     * Update project
     * @param p
     */
    @Update
    fun update(p: Project)

    /**
     * Delete Project
     * @param p id must be set
     */
    @Delete
    fun delete(p: Project)

    /**
     * Insert project into db
     * @param p
     * @return projectID
     */
    @Insert
    fun insert(p: Project): Long
    //groups
    /**
     * Get all groups for project
     * @param id projectID
     * @return List of groups in that project
     */
    @Query("Select * from `Group` Where projectID=:id")
    fun getProjectGroups(id: Long): MutableList<Group>?

    /**
     * Gets all groups, currently unused
     * @return
     */
    @get:Query("Select * from `Group`")
    val groups: List<Group>

    /**
     * Update group
     * @param g
     */
    @Update
    fun update(g: Group)

    /**
     * Delete group by ID
     * @param id groupID
     */
    @Query("Delete from `group` where id=:id")
    fun deleteGroup(id: Long)

    @Insert
    fun insert(g: Group): Long

    /**
     * Delete Projects Groups
     * @param id projectID
     */
    @Query("delete from `group` where projectID=:id")
    fun deleteAllProjectsGroups(id: Long)
    //keybinds
    /*@Query("Select * from keybind")
    List<Keybind> getKeybinds();*/
    /**
     * Gets all keybinds for a group row
     * @param id group ID
     * @return List of keybinds for group
     */
    @Query("Select * from 'keybind' where groupID=:id")
    fun getGroupKeybinds(id: Long): MutableList<Keybind>

    /**
     * Updates the keybind in the room db
     * @param k keybind to update
     */
    @Update
    fun update(k: Keybind)

    /**
     * Deletes keybind from db
     * @param k Keybind to delete
     */
    @Delete
    fun delete(k: Keybind)

    /**
     * Inserts keybind into db
     * @param k keybind to insert
     * @return keybind row ID
     */
    @Insert
    fun insert(k: Keybind): Long

    /**
     * Deletes all keybinds with group id
     * @param id Group ID
     */
    @Query("delete from keybind where groupID=:id")
    fun deleteGroupKeybinds(id: Long)
}