package com.example.keybindhelper.Dialogs;

import android.app.Dialog;
import android.content.Context;

import com.example.keybindhelper.R;

public class ArrowProvider {
    public DirectionClicked directionClicked;
    private Dialog d;
    public ArrowProvider(Context context){
        d=new Dialog(context);
        d.setContentView(R.layout.move_item_dialog);
        d.findViewById(R.id.move_up_button).setOnClickListener(z->{
            if(directionClicked!=null)
                directionClicked.Clicked(true);
        });
        d.findViewById(R.id.move_down_button).setOnClickListener(z->{
            if(directionClicked!=null)
                directionClicked.Clicked(false);
        });
    }
    public void Show(){
        d.show();
    }
    public interface DirectionClicked{
        void Clicked(Boolean isUp);
    }
}
