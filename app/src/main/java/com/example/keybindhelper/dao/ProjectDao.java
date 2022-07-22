package com.example.keybindhelper.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;
import com.example.keybindhelper.dto.ThemeDTO;

import java.util.List;

@Dao
public interface ProjectDao {

    //projects

    /**
     * Get all projects
     * @return returns all projects
     */
    @Query("Select * from Project")
    List<Project> getProjects();

    /**
     * Update project
     * @param p
     */
    @Update
    void update(Project p);

    /**
     * Delete Project
     * @param p id must be set
     */
    @Delete
    void delete(Project p);

    /**
     * Insert project into db
     * @param p
     * @return projectID
     */
    @Insert
    long insert(Project p);

//groups

    /**
     * Get all groups for project
     * @param id projectID
     * @return List of groups in that project
     */
    @Query("Select * from `Group` Where projectID=:id")
    List<Group> getProjectGroups(long id);

    /**
     * Gets all groups, currently unused
     * @return
     */
    @Query("Select * from `Group`")
    List<Group> getGroups();

    /**
     * Update group
     * @param g
     */
    @Update
    void update(Group g);

    /**
     * Delete group by ID
     * @param id groupID
     */
    @Query("Delete from `group` where id=:id")
    void deleteGroup(long id);
    @Insert
    long insert(Group g);

    /**
     * Delete Projects Groups
     * @param id projectID
     */
    @Query("delete from `group` where projectID=:id")
    void deleteAllProjectsGroups(long id);

//keybinds
    /*@Query("Select * from keybind")
    List<Keybind> getKeybinds();*/

    /**
     * Gets all keybinds for a group row
     * @param id group ID
     * @return List of keybinds for group
     */
    @Query("Select * from 'keybind' where groupID=:id")
    List<Keybind> getGroupKeybinds(long id);

    /**
     * Updates the keybind in the room db
     * @param k keybind to update
     */
    @Update
    void update(Keybind k);

    /**
     * Deletes keybind from db
     * @param k Keybind to delete
     */
    @Delete
    void delete(Keybind k);

    /**
     * Inserts keybind into db
     * @param k keybind to insert
     * @return keybind row ID
     */
    @Insert
    long insert(Keybind k);

    /**
     * Deletes all keybinds with group id
     * @param id Group ID
     */
    @Query("delete from keybind where groupID=:id")
    void deleteGroupKeybinds(long id);

    //Theme

    /**
     * Inserts the theme if it does not already exist
     * @param theme
     */
    @Insert
    void insert(ThemeDTO theme);

    /**
     * Updates the current theme
     */
    @Update
    void update(ThemeDTO theme);

    @Query("select * from ThemeDTO limit 1")
    ThemeDTO getThemeDTO();


}
