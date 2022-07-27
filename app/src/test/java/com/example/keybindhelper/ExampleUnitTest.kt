package com.example.keybindhelper

import com.example.keybindhelper.dto.Group
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun Group_Test(){
        val g=Group()
        for (i in 0..5){
            g.AddKeybind()
        }
        println("keybind count: "+g.keybinds.size)
        assertEquals(g.keybinds.size,6);
    }
}