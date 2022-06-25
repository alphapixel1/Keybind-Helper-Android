package com.example.keybindhelper.Dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import com.example.keybindhelper.R
import android.widget.TextView
import com.example.keybindhelper.Dialogs.GroupListProvider.GroupClick
import android.widget.LinearLayout
import android.widget.ScrollView
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.google.android.material.textfield.TextInputEditText

class ConfirmDialog(c: Context?, title: String?) {
    @JvmField
    var onConfirmed: ConfirmedEvent? = null
    private val dialog: Dialog
    fun Show() {
        dialog.show()
    }

    interface ConfirmedEvent {
        fun onConfirmed()
    }

    init {
        dialog = Dialog(c!!)
        dialog.setContentView(R.layout.confirm_dialog)
        (dialog.findViewById<View>(R.id.confirm_dialog_title) as TextView).text = title
        dialog.findViewById<View>(R.id.confirm_dialog_cancel_btn)
            .setOnClickListener { v: View? -> dialog.cancel() }
        dialog.findViewById<View>(R.id.confirm_dialog_ok_btn).setOnClickListener { v: View? ->
            dialog.cancel()
            onConfirmed!!.onConfirmed()
        }
    }
}