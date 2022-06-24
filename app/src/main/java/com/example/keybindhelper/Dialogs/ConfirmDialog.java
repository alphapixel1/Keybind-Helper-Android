package com.example.keybindhelper.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.example.keybindhelper.R;

public class ConfirmDialog {
    public ConfirmedEvent onConfirmed;
    private Dialog dialog;
    public ConfirmDialog(Context c,String title){
        dialog=new Dialog(c);
        dialog.setContentView(R.layout.confirm_dialog);
        ((TextView)dialog.findViewById(R.id.confirm_dialog_title)).setText(title);
        dialog.findViewById(R.id.confirm_dialog_cancel_btn).setOnClickListener(v->{
            dialog.cancel();
        });
        dialog.findViewById(R.id.confirm_dialog_ok_btn).setOnClickListener(v->{
            dialog.cancel();
            onConfirmed.onConfirmed();
        });
    }
    public void Show(){
        dialog.show();
    }
    public interface ConfirmedEvent{
        void onConfirmed();
    }
}
