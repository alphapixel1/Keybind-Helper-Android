package com.example.keybindhelper.Dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import com.example.keybindhelper.R
import android.widget.TextView

class ConfirmDialog(c: Context?, title: String?) {
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
        dialog.findViewById<View>(R.id.confirm_dialog_ok_btn).setOnClickListener {
            dialog.cancel()
            onConfirmed!!.onConfirmed()
        }
    }
}