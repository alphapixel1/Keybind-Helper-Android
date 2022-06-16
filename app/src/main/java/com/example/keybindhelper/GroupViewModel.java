package com.example.keybindhelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.keybindhelper.PromptDialog.PromptDialog;
import com.example.keybindhelper.PromptDialog.ValidatorResponse;

import java.util.ArrayList;
import java.util.List;

public class GroupViewModel {
    public View view;
    //public CardView Box;
   // private LinearLayout MainLayout,TopLayout;
    private Button GroupNameButton;
    private ImageButton AddButton,MoreButton;
    static final int ButtonWidth=168;
    private LinearLayout keybindZone;
    public KeybindGroup keybindGroup;

    public GroupViewModel(Context context, KeybindGroup group){

        keybindGroup=group;
        view = LayoutInflater.from(context).inflate(R.layout.group_view,null);
        AddButton= view.findViewById(R.id.group_add_button);
        MoreButton= view.findViewById(R.id.group_more_button);
        GroupNameButton= view.findViewById(R.id.group_name_button);
        keybindZone= view.findViewById(R.id.keybind_zone);
        GroupNameButton.setOnClickListener(v -> {
            PromptDialog pd= new PromptDialog(
                    context,
                    "Rename Group",
                    "",
                    group.getName()
            );
            pd.validation= text -> new ValidatorResponse(text.equals(group.getName()) || GroupsStorage.isGroupNameAvailable(text),
                    "Name Has Already Been Taken");
            pd.confirmedEvent= group::SetName;
            pd.ShowDialog();
        });
        AddButton.setOnClickListener(v->{
            group.AddKeybind();
            if(keybindZone.getVisibility()!=View.VISIBLE)
                keybindZone.setVisibility(View.VISIBLE);
        });
        MoreButton.setOnClickListener(z->{
            Dialog d=new Dialog(context);
            d.setContentView(R.layout.group_menu);
            ((TextView)d.findViewById(R.id.group_name)).setText(group.getName());
            d.findViewById(R.id.group_clear_keybinds).setOnClickListener(v->{
                group.Keybinds.clear();
                keybindZone.removeAllViews();
                d.cancel();
            });
            d.findViewById(R.id.group_delete).setOnClickListener(v->{
                GroupsStorage.Groups.remove(group);
                ((LinearLayout)view.getParent()).removeView(view);
                d.cancel();
            });
            d.findViewById(R.id.group_showhide).setOnClickListener(v->{
                keybindZone.setVisibility(keybindZone.getVisibility()==View.VISIBLE? View.GONE:View.VISIBLE);
                d.cancel();
            });
            d.findViewById(R.id.group_copy).setOnClickListener(v->{
                KeybindGroup g=group.Clone();
                GroupsStorage.Groups.add(g);
                ((LinearLayout)view.getParent()).addView(g.model.view);
                d.cancel();
            });
            d.findViewById(R.id.group_dissolve).setOnClickListener(v->{
                List<KeybindGroup> groups= new ArrayList<>();
                for (KeybindGroup g:GroupsStorage.Groups) {
                    if(g!=group)
                        groups.add(g);
                }
                GroupListProvider glp=new GroupListProvider(context,"Dissolve Group Into",groups);
                d.cancel();
                glp.groupClick=newGroup->{
                    for (Keybind kb:group.Keybinds.toArray(new Keybind[0])) {
                        newGroup.AddKeybind(kb);
                    }
                    GroupsStorage.Groups.remove(group);
                    ((LinearLayout)view.getParent()).removeView(view);
                };
                glp.Show();
            });
            d.findViewById(R.id.group_move).setOnClickListener(v->{
                d.cancel();
                ArrowProvider ap=new ArrowProvider(context);
                ap.directionClicked=isUp -> {
                    int indx=GroupsStorage.Groups.indexOf(group);
                    System.out.println(indx);
                    LinearLayout parent=(LinearLayout) view.getParent();
                    if(isUp){
                        if(indx>0) {
                            GroupsStorage.Groups.remove(group);
                            GroupsStorage.Groups.add(indx - 1, group);
                            parent.removeView(view);
                            parent.addView(view, indx - 1);
                        }
                    }else{
                        if(indx<GroupsStorage.Groups.size()-1){
                            GroupsStorage.Groups.remove(group);
                            GroupsStorage.Groups.add(indx + 1, group);
                            parent.removeView(view);
                            parent.addView(view, indx +  1);
                        }
                    }
                };
                ap.Show();
            });
            d.show();

        });



        GroupNameButton.setText(group.getName());



    }

    public void UpdateName(String name) {
        if(GroupNameButton!=null)
            GroupNameButton.setText(name);
    }

    public void AddKeybind(Keybind ret) {
        keybindZone.addView(ret.model.view);
    }
}
