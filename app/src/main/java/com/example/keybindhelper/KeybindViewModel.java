package com.example.keybindhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class KeybindViewModel {
    public Keybind keybind;
    public View view;
    private TextView nameTV,kb1TV,kb2TV,kb3TV;
    private CardView kb1CV,kb2CV,kb3CV;
    public KeybindViewModel(Context context,Keybind keybind){
        this.keybind=keybind;
        view= LayoutInflater.from(context).inflate(R.layout.keybind_view,null);
        nameTV=view.findViewById(R.id.keybind_name);

        kb1TV=view.findViewById(R.id.keybind_1_text);
        kb2TV=view.findViewById(R.id.keybind_2_text);
        kb3TV=view.findViewById(R.id.keybind_3_text);

        kb1CV=view.findViewById(R.id.keybind_1_card);
        kb2CV=view.findViewById(R.id.keybind_2_card);
        kb3CV=view.findViewById(R.id.keybind_3_card);

        LinearLayout main =view.findViewById(R.id.keybind_main_layout);
        main.setOnClickListener(v -> {
            keybind.showEditDialog();
        });

        main.setLongClickable(true);  //if keybind is held down, show option menu
        main.setOnLongClickListener(v -> {
            keybind.showContextMenu();
            return true;
        });
        Update();

    }
    public void Update(){
        nameTV.setText(keybind.name);

        String[] kbs=new String[]{keybind.kb1,keybind.kb2,keybind.kb3};
        TextView[] tvs=new TextView[]{kb1TV,kb2TV,kb3TV};  //update UI keybind text with input keybinds
        CardView[] cvs=new CardView[]{kb1CV,kb2CV,kb3CV};  //update UI input keybind card with input keybinds
        for (int i=0;i<kbs.length;i++){
            String kb=kbs[i];
            tvs[i].setText(kb);
           if(kb==""){ //Don't show empty keybind values
               cvs[i].setVisibility(View.GONE);
           }else{
               cvs[i].setVisibility(View.VISIBLE);
           }
        }
    }
}
