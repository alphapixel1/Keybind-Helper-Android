package com.example.keybindhelper

import android.app.Instrumentation
import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.impl.annotations.MockK
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
public class KeybindHelperTest {

    var groups = ArrayList<KeybindGroup>()

    @MockK
    lateinit var context : Context

    lateinit var groupListProvider : GroupListProvider

    @get: Rule
    var rule :  TestRule = InstantTaskExecutorRule()

    @Test
    fun givenKeybindsExist_whenUserCopies_ThenKeybindsAreCloned(){
        givenKeyBindsExist()
    }

    @Test
    fun givenKeyBindsExist() {
        var g = KeybindGroup(context)
        g.SetName("Vibeo Gane")
        val k: Keybind = g.AddKeybind()
        k.kb1 = "Tab"
        k.kb2 = "Alt"
        k.kb3 = "Any"
        k.name = "Not Konami Code"
        assert(groups.isNotEmpty())
    }
}