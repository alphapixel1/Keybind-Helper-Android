package com.example.keybindhelper

import com.example.keybindhelper.Keybind
import org.junit.Assert.*
import org.junit.Test

class KeybindTests {

    @Test
    fun confirmKeybind()    {
        val bind : Keybind = Keybind("Test","A","B","C")
        assertEquals("Test", bind.toString())
    }
}