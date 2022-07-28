package com.example.keybindhelper

//import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import com.example.keybindhelper.dto.Group
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dto.Project

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

    private fun get_Group_With_N_Keybinds(keybind_count: Int):Group{
       val g=Group();
       for (i in 0 until keybind_count)
           g.addKeybind()
       return g
    }
    @Test
    fun group_Constructor_Test(){
        val g=Group()
        assertEquals(g.keybinds.size,0);
    }
    @Test
    fun project_Constructor_Test(){
        val p=Project();
        assertEquals(p.Groups.size,0);
    }
    @Test
    fun project_add_group(){
        val p=Project();
        p.Groups.add(Group())
        assert(p.Groups.size==1)
    }
    @Test
    fun group_add_keybind(){
        val g=get_Group_With_N_Keybinds(1)
        assert(g.keybinds.size==1)
    }
    @Test
    fun group_Move_Keybind_Up(){
        val g=get_Group_With_N_Keybinds(3);
        val middle_keybind=g.keybinds[1]
        g.moveKeybindUpDown(middle_keybind,-1)
        assertEquals(g.keybinds.indexOf(middle_keybind),0)
    }
    @Test
    fun group_Move_Keybind_Down(){
        val g=get_Group_With_N_Keybinds(3);
        val middle_keybind=g.keybinds[1]
        g.moveKeybindUpDown(middle_keybind,1)
        assertEquals(g.keybinds.indexOf(middle_keybind),2)
    }
    @Test
    fun group_delete_Keybind(){
        val g= get_Group_With_N_Keybinds(2)
        g.deleteKeybind(g.keybinds[1])
        assertEquals(g.keybinds.size,1)
    }

    /**
     * Moves keybind 0 to 1 position then updates their indexes and checks if the indexes are in order
     */
    @Test
    fun update_Keybinds(){
        val g=get_Group_With_N_Keybinds(3)
        g.moveKeybindUpDown(g.keybinds[0],1)
        g.updateKeybinds()
        for (i in 0 until g.keybinds.size)
            if(g.keybinds[i].index!=i)
                assert(false)
        assert(true);
    }
    @Test
    fun add_existing_keybind(){
        val g=get_Group_With_N_Keybinds(2)
        val currentCount=g.keybinds.size;
        val k=Keybind()
        g.addKeybind(k,false)
        assertEquals(g.keybinds.size,currentCount+1)
    }
    @Test
    fun group_unload_stored_views(){
        val g=get_Group_With_N_Keybinds(2)
        g.unloadStoredViews()
    }
    @Test
    fun c(){

    }
}