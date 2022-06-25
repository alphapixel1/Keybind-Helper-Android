package com.example.keybindhelper.Dialogs

import com.example.keybindhelper.R
import android.widget.TextView
import com.example.keybindhelper.Dialogs.GroupListProvider.GroupClick
import android.widget.LinearLayout
import android.widget.ScrollView
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.google.android.material.textfield.TextInputEditText

data class ValidatorResponse(var isValid: Boolean, var invalidMessage: String)