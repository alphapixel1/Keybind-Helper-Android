package com.example.keybindhelper.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.keybindhelper.R;
import com.google.android.material.textfield.TextInputEditText;


public class PromptDialog {
    private final Dialog dialog;
    public Validator validation;
    public ConfirmedEvent confirmedEvent;
    public Boolean cannotBeEmpty=true;
    public PromptDialog(Context context, String title, String content, String currentValue,String hint){
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.promptdialog);


        TextView titleView=dialog.findViewById(R.id.promptText);
        TextView contentView=dialog.findViewById(R.id.PromptContent);
        TextInputEditText inputBox=dialog.findViewById(R.id.PromptInputBox);

        TextView errorView=dialog.findViewById(R.id.PromptErrorText);
        titleView.setText(title);
        contentView.setText(content);
        inputBox.setText(currentValue);
        inputBox.setSelectAllOnFocus(true);
        inputBox.requestFocus();

        if(title==null || title.length()==0)
            titleView.setVisibility(View.GONE);
        if(content==null || content.length()==0)
            contentView.setVisibility(View.GONE);
        if(hint!=null)
            inputBox.setHint(hint);
        inputBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        dialog.findViewById(R.id.PromptOkButton).setOnClickListener(v -> {
            String text=inputBox.getText().toString();
            if(validation!=null){
                ValidatorResponse vr=validation.Validate(text);
                if(cannotBeEmpty && text.length()==0){
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(R.string.input_cannot_be_empty);
                    return;
                }
                if(vr.isValid){
                    imm.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);
                    dialog.cancel();
                    if(confirmedEvent!=null)
                        confirmedEvent.onConfirmed(text);
                }else{
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(vr.invalidMessage);
                }
            }
        });
        dialog.findViewById(R.id.promptCancelButton).setOnClickListener(v->{
            imm.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);
            dialog.cancel();
        });

    }
    public void ShowDialog(){
        dialog.show();
    }
    public interface Validator{
      ValidatorResponse Validate(String text);
    }
    public interface ConfirmedEvent{
        void onConfirmed(String text);
    }


}
