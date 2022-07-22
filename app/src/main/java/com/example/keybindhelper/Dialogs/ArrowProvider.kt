package com.example.keybindhelper.Dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import com.example.keybindhelper.Dialogs.ArrowProvider.DirectionClicked
import com.example.keybindhelper.R

class ArrowProvider(context: Context?) {
    var directionClicked: DirectionClicked? = null
    private val d: Dialog
    fun Show() {
        d.show()
    }

    interface DirectionClicked {
        fun Clicked(isUp: Boolean?)
    }

    init {
        d = Dialog(context!!)
        d.setContentView(R.layout.move_item_dialog)
        d.findViewById<View>(R.id.move_up_button).setOnClickListener {
            if (directionClicked != null) directionClicked!!.Clicked(true)
        }
        d.findViewById<View>(R.id.move_down_button).setOnClickListener {
            if (directionClicked != null) directionClicked!!.Clicked(false)
        }
    }
}