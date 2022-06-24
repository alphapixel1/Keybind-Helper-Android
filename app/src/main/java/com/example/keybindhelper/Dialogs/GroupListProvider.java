package com.example.keybindhelper.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.example.keybindhelper.dto.Group;

import java.util.List;

public class GroupListProvider {
    public GroupClick groupClick;
    private Dialog d;
    public GroupListProvider(Context c, String title, List<Group> groups){
        d=new Dialog(c);
        LinearLayout line=new LinearLayout(c);
        line.setOrientation(LinearLayout.VERTICAL);
        line.setPadding(40,40,40,40);
        d.setContentView(line);

        TextView titleView=new TextView(c);
        titleView.setText(title);
        titleView.setTextSize(30f);

        line.addView(titleView);


        ScrollView s=new ScrollView(c);
        line.addView(s);
        LinearLayout scrollLine=new LinearLayout(c);
        scrollLine.setOrientation(LinearLayout.VERTICAL);
        s.addView(scrollLine);

        for (Group g:groups) {
            Group cg=g;
            Button b=new Button(c);
            b.setText(g.name);
            b.setOnClickListener(v->{
                groupClick.GroupClicked(cg);
                d.cancel();
            });
            scrollLine.addView(b);
        }
        if(groups.size()==0){
            TextView noitems=new TextView(c);
            noitems.setText("None");
            noitems.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            line.addView(noitems);
        }
    }
    public void Show(){
        d.show();
    }
    public interface GroupClick{
        void GroupClicked(Group g);
    }
}
