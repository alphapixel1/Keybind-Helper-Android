package com.example.keybindhelper.Theme

import android.provider.ContactsContract
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R.color
import com.example.keybindhelper.dao.DatabaseManager

object ThemeManager {
    var CurrentTheme: Theme?=null;
    private var mainActivity: MainActivity?=null;
    val Themes:List<Theme> = listOf(
        Theme("Default",
            color.black,//app color
            color.white,//icon color
            color.white,//background color
            color.black,//text color
            color.white,//group header color
            color.group_background,//keybind background color
            color.offset_keybind_background//keybind offset color
        ),
        Theme("Demo",
            color.black,//app color
            color.white,//icon color
            color.white,//background color
            color.black,//text color
            color.white,//group header color
            color.group_background,//keybind background color
            color.offset_keybind_background//keybind offset color
        )
    )

    /**
     * To be Initialized after the database
     */
    fun init(mainActivity:MainActivity){
        assert(DatabaseManager.db!=null)//checking that the database has been initalized
        val dto=DatabaseManager.db.themeDTO
        System.err.println("ThemeManager.init: make sure to make a table for theme settings");
        CurrentTheme= Themes[0];
        this.mainActivity=mainActivity;
    }
    fun applyTheme(){
        mainActivity?.applyTheme();
    }
}
data class Theme(val name:String,
                 val appColor:Int,
                 val iconColor:Int,
                 val backgroundColor:Int,
                 val textColor:Int,
                 val groupHeaderColor:Int,
                 val keybindBackgroundColor:Int,
                 val offsetKeybindBackgroundColor:Int,
                )
