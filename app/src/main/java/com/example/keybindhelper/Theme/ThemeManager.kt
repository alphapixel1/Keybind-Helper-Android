package com.example.keybindhelper.Theme

import com.example.keybindhelper.R.color

object ThemeManager {
    val CurrentTheme: Theme?=null;
    val Themes:List<Theme> = listOf(
        Theme("Default",
            color.black,//app color
            color.white,//icon color
            color.white,//background color
            color.black,//text color
            color.white,//group header color
            color.group_background,//keybind background color
            color.offset_keybind_background//keybind offset color
        )
    )
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
