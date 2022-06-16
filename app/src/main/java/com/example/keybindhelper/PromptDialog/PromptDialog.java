package com.example.keybindhelper.PromptDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.keybindhelper.R;

import org.w3c.dom.Text;

public class PromptDialog {
    private Dialog dialog;
    public Validator validation;
    public ConfirmedEvent confirmedEvent;
    public Boolean cannotBeEmpty=true;
    private Button cancelBtn,doneBtn;
    private EditText inputBox;
    private TextView errorView;
    public PromptDialog(Context context, String title, String content, String currentValue){
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.promptdialog);
        cancelBtn=dialog.findViewById(R.id.promptCancelButton);
        doneBtn=dialog.findViewById(R.id.PromptOkButton);
        TextView titleView=dialog.findViewById(R.id.promptText);
        TextView contentView=dialog.findViewById(R.id.PromptContent);
        inputBox=dialog.findViewById(R.id.PromptInputBox);
        inputBox.requestFocus();
        errorView=dialog.findViewById(R.id.PromptErrorText);
        titleView.setText(title);
        contentView.setText(content);
        inputBox.setText(currentValue);
        if(title.length()==0)
            titleView.setVisibility(View.GONE);
        if(content.length()==0)
            contentView.setVisibility(View.GONE);
    }
    public void ShowDialog(){
        dialog.show();
        doneBtn.setOnClickListener(v -> {
            String text=inputBox.getText().toString();
            if(validation!=null){
                ValidatorResponse vr=validation.Validate(text);
                if(cannotBeEmpty && text.length()==0){
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText("Input Cannot Be Empty.");
                    return;
                }
                if(vr.isValid){
                    dialog.dismiss();
                    if(confirmedEvent!=null)
                        confirmedEvent.onConfirmed(text);
                }else{
                    errorView.setText(vr.invalidMessage);
                }
            }
        });
        cancelBtn.setOnClickListener(v->{
            dialog.cancel();
        });
    }
    public interface Validator{
      ValidatorResponse Validate(String text);
    }
    public interface ConfirmedEvent{
        void onConfirmed(String text);
    }
}
