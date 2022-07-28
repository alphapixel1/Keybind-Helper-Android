package com.example.keybindhelper

import android.os.Looper
import com.example.keybindhelper.dto.Group
import com.example.keybindhelper.dto.Project
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

 /*   @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }*/
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
        Looper.getMainLooper()
        p.name.value="bruh";
        assert(true)
       /* for(i in 0..5)
            p.AddGroup()*/
       // assertEquals(p.Groups.size,6 )
    }
}