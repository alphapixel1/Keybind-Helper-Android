package com.example.keybindhelper.Dialogs

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.keybindhelper.ITaskResponse
import com.example.keybindhelper.R
import java.util.zip.Inflater

class ButtonDialogProvider {
    private val context:Context;
    private val dialog: Dialog;
    constructor(context:Context,title:String,buttons:List<String>,response: ITaskResponse<String>){
        this.context=context;
        this.dialog= Dialog(context);
        dialog.setContentView(R.layout.buttondialogprovider_layout);

        val linearLayout=dialog.findViewById<LinearLayout>(R.id.buttondialogprovider_linear_layout);
        linearLayout.minimumWidth=800;
        linearLayout.orientation=LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(10, 10, 10, 10)
        linearLayout.layoutParams=layoutParams;


        val tv=TextView(context);
        tv.text=title;
        tv.textAlignment=TextView.TEXT_ALIGNMENT_CENTER;
        tv.textSize=24f
        tv.layoutParams=layoutParams;
        linearLayout.addView(tv);


        for (b in buttons) {
            val button = Button(context);
            linearLayout.addView(button);
            button.setOnClickListener {
                dialog.cancel()
                response.onResponse(b)
            }
            button.text = b;
        }
    }
    fun show(){
        dialog.show()
    }
    fun cancel(){
        dialog.cancel()
    }
}