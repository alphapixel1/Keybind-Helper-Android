package com.example.keybindhelper

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

data class Keybind(var Name : String, var KB1 : String, var KB2 : String, var KB3: String) {

    override fun toString(): String {
        return Name
    }

    fun ShowEditKeybindDailog(context: Context){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.edit_keybind_dialog)

        dialog.show()



        //clear buttons
        val clears= arrayOf(0,R.id.EditKeybind_Clear1,R.id.EditKeybind_Clear2,R.id.EditKeybind_Clear3)
        //dialog text views
        val texts= arrayOf(R.id.EditKeybind_Name,R.id.EditKeybind_KB1,R.id.EditKeybind_KB2,R.id.EditKeybind_KB3)

        //loading current text
        /*for(i in 0..3)
            dialog.findViewById<TextView>(texts[i]).text=TVs[i].text*/
        dialog.findViewById<TextView>(texts[0]).text=Name
        dialog.findViewById<TextView>(texts[1]).text=KB1
        dialog.findViewById<TextView>(texts[2]).text=KB2
        dialog.findViewById<TextView>(texts[3]).text=KB3


        //clear button clicks
        for (i in 1..3){
            println(i)
            dialog.findViewById<ImageButton>(clears[i]).setOnClickListener{
                dialog.findViewById<TextView>(texts[i]).text=""
            }
        }

        //done button
        val doneButton=dialog.findViewById<Button>(R.id.EditKeybindDoneButton)
        doneButton.setOnClickListener{
            Name=dialog.findViewById<TextView>(texts[0]).text.toString()
            KB1=dialog.findViewById<TextView>(texts[1]).text.toString()
            KB2=dialog.findViewById<TextView>(texts[2]).text.toString()
            KB3=dialog.findViewById<TextView>(texts[3]).text.toString()
            dialog.cancel()
        }

        //cancel button
        val cancelButton=dialog.findViewById<Button>(R.id.EditKeybindCancelButton)
        cancelButton.setOnClickListener{
            dialog.cancel()
        }
    }
}