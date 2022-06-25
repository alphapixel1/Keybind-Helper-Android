package com.example.keybindhelper.Dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.example.keybindhelper.R
import com.google.android.material.textfield.TextInputEditText

class PromptDialog(
    context: Context?,
    title: String?,
    content: String?,
    currentValue: String?,
    hint: String?
) {
    private val dialog: Dialog
    @JvmField
    var validation: Validator? = null
    @JvmField
    var confirmedEvent: ConfirmedEvent? = null
    var cannotBeEmpty = true
    fun ShowDialog() {
        dialog.show()
    }

    interface Validator {
        fun Validate(text: String?): ValidatorResponse
    }

    interface ConfirmedEvent {
        fun onConfirmed(text: String?)
    }

    init {
        dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.promptdialog)
        val titleView = dialog.findViewById<TextView>(R.id.promptText)
        val contentView = dialog.findViewById<TextView>(R.id.PromptContent)
        val inputBox = dialog.findViewById<TextInputEditText>(R.id.PromptInputBox)
        val errorView = dialog.findViewById<TextView>(R.id.PromptErrorText)
        titleView.text = title
        contentView.text = content
        inputBox.setText(currentValue)
        inputBox.setSelectAllOnFocus(true)
        inputBox.requestFocus()
        if (title == null || title.length == 0) titleView.visibility = View.GONE
        if (content == null || content.length == 0) contentView.visibility = View.GONE
        if (hint != null) inputBox.hint = hint
        inputBox.requestFocus()
        val imm =
            dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        dialog.findViewById<View>(R.id.PromptOkButton).setOnClickListener { v: View? ->
            val text = inputBox.text.toString()
            if (validation != null) {
                val vr = validation!!.Validate(text)
                if (cannotBeEmpty && text.length == 0) {
                    errorView.visibility = View.VISIBLE
                    errorView.setText(R.string.input_cannot_be_empty)
                    return@setOnClickListener
                }
                if (vr.isValid) {
                    imm.hideSoftInputFromWindow(inputBox.windowToken, 0)
                    dialog.cancel()
                    if (confirmedEvent != null) confirmedEvent!!.onConfirmed(text)
                } else {
                    errorView.visibility = View.VISIBLE
                    errorView.text = vr.invalidMessage
                }
            }
        }
        dialog.findViewById<View>(R.id.promptCancelButton).setOnClickListener { v: View? ->
            imm.hideSoftInputFromWindow(inputBox.windowToken, 0)
            dialog.cancel()
        }
    }
}