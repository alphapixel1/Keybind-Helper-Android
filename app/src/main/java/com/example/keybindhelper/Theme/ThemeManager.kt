package com.example.keybindhelper.Theme

import android.provider.ContactsContract
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R.color
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dto.ThemeDTO

object ThemeManager {
    var CurrentTheme: Theme?=null;
    private var mainActivity: MainActivity?=null;
    val Themes:List<Theme> = listOf(
        Theme("Default",
            color.black,//app color
            color.white,//icon color/text
            color.dark_gray,//background color
            color.group_header,//group header color
            color.group_background,//keybind background color
            color.offset_keybind_background,//keybind offset color
            color.black//keybind card color
        ),
        Theme("Inverted",
            color.white,//app color
            color.black,//icon color/text
            color.white,//background color
            color.light_white,//group header color
            color.lighter_white,//keybind background color
            color.light_white,//keybind offset color
            color.white//keybind card color
        ),
        Theme("Tropical",
            color.white,//app color
            color.purple,//icon color/text
            color.lime,//background color
            color.watermelon_red,//group header color
            color.mango,//keybind background color
            color.banana,//keybind offset color
            color.watermelon_pink//keybind card color
        )
    )

    /**
     * To be Initialized after the database
     */
    fun init(mainActivity:MainActivity){
        assert(DatabaseManager.db!=null)//checking that the database has been initalized
        val dto=DatabaseManager.db!!.themeDTO
        if(dto==null){
            val t=ThemeDTO()
            DatabaseManager.db!!.insert(t);
            CurrentTheme= Themes[0];
        }else{
            val savedTheme=Themes.find { it.name==dto.ThemeName }
            if(savedTheme!=null)
                CurrentTheme=savedTheme;
            else
                CurrentTheme=Themes[0]
        }

       // println("THEME $dto")
        System.err.println("ThemeManager.init: make sure to make a table for theme settings");

        this.mainActivity=mainActivity;
    }
    fun applyTheme(){
        var t=ThemeDTO();
        t.ThemeName= CurrentTheme!!.name;
        DatabaseManager.db!!.update(t)
        mainActivity?.applyTheme();
    }
}
data class Theme(val name:String,
                 val appColor:Int,
                 val iconColor:Int,
                 val backgroundColor:Int,
                 val groupHeaderColor:Int,
                 val keybindBackgroundColor:Int,
                 val offsetKeybindBackgroundColor:Int,
                 val keybindCardColor:Int
                )
