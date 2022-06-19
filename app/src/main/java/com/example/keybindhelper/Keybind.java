package com.example.keybindhelper;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Entity
public class Keybind {

    @ColumnInfo(name="name")
    public  String name;
    @ColumnInfo(name="Kb1")
    public String kb1="";
    @ColumnInfo(name="Kb2")
    public String kb2="";
    @ColumnInfo(name="Kb3")
    public String kb3="";
    public KeybindGroup group;
    @ColumnInfo(name="GroupID")
    public int getGroupID(){
        return group.ID;
    }
    @ColumnInfo(name = "ProjectID")
    public int GetProjectID(){
        return Project.ProjectID;
    }
    @ColumnInfo(name="Index")
    public int GetIndex(){return group.Keybinds.indexOf(this);}
    public KeybindViewModel model;
    private Context context;

    public Keybind(Context context,KeybindGroup group){
        this.context = context;
        name= Project.GetFirstKeybindUnnamed();
        this.group=group;
        model =new KeybindViewModel(context,this);
        model.Update();
    }
    public Keybind(Context context,String name,String kb1,String kb2,String kb3,KeybindGroup group){
        this.context=context;
        this.name = name;
        this.kb1 = kb1;
        this.kb2 = kb2;
        this.kb3 = kb3;
        this.group=group;
        model =new KeybindViewModel(context,this);
        model.Update();
    }
    public void showEditDialog(){
        Dialog d=new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(true);
        d.setContentView(R.layout.edit_keybind_dialog);

        int[] clears= new int[]{R.id.editKeybind_Clear0,R.id.EditKeybind_Clear1,R.id.EditKeybind_Clear2,R.id.EditKeybind_Clear3};
        //dialog text views
        int[] texts= new int[]{R.id.EditKeybind_Name,R.id.EditKeybind_KB1,R.id.EditKeybind_KB2,R.id.EditKeybind_KB3};

        //loading current text
        ((EditText)d.findViewById(texts[0])).setText(name);
        ((EditText)d.findViewById(texts[1])).setText(kb1);
        ((EditText)d.findViewById(texts[2])).setText(kb2);
        ((EditText)d.findViewById(texts[3])).setText(kb3);

        //clear button clicks
        for (int i=0;i<4;i++){
            int id=texts[i];
            (d.findViewById(clears[i])).setOnClickListener(v->{
                ((TextView)d.findViewById(id)).setText("");
            });
        }
        //cancel button
        d.findViewById(R.id.EditKeybindCancelButton).setOnClickListener(v -> {
            d.cancel();
        });


        d.findViewById(R.id.EditKeybindCancelButton).setOnLongClickListener(v -> {
            System.out.println("fuck");
            return true;
        });

        //done button
        d.findViewById(R.id.EditKeybindDoneButton).setOnClickListener(v->{
            String n=((EditText)d.findViewById(texts[0])).getText().toString();
            TextView error=d.findViewById(R.id.keybind_error_text);
            if(n.length()==0) {
                error.setVisibility(View.VISIBLE);
                error.setText("Name Cannot Be Empty");
            }else{
                name=n;

                String kb1T=((EditText)d.findViewById(texts[1])).getText().toString();
                String kb2T=((EditText)d.findViewById(texts[2])).getText().toString();
                String kb3T=((EditText)d.findViewById(texts[3])).getText().toString();

                List<String> kbs= Arrays.asList(kb1T,kb2T,kb3T);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    kbs=kbs.stream().filter(z->z.length()>0).collect(Collectors.toList());
                    kb1=(kbs.size()>0)? kbs.get(0):"";
                    kb2=(kbs.size()>1)? kbs.get(1):"";
                    kb3=(kbs.size()>2)? kbs.get(2):"";
                }else{
                    kb1=kb1T;
                    kb2=kb2T;
                    kb3=kb3T;
                }
                d.cancel();
                model.Update();
            }
        });

        d.show();
   }

    public void showContextMenu() {
        Dialog d=new Dialog(context);
        d.setContentView(R.layout.keybind_settings_dialog);
        ((TextView)d.findViewById(R.id.keybind_settings_name)).setText(name);
        d.findViewById(R.id.keybind_copy).setOnClickListener(v->{
            group.AddKeybind(Clone(false));
            d.cancel();
        });
        d.findViewById(R.id.keybind_delete).setOnClickListener(v -> {
            ((LinearLayout)model.view.getParent()).removeView(model.view);
            group.Keybinds.remove(this);
            d.cancel();
        });

        d.findViewById(R.id.keybind_move).setOnClickListener(z->{
            d.cancel();
            ArrowProvider ap=new ArrowProvider(context);
            ap.directionClicked=isUp -> {
                int indx=group.Keybinds.indexOf(this);
                System.out.println(indx);
                LinearLayout parent=(LinearLayout) model.view.getParent();
                if(isUp){
                    if(indx>0) {
                        group.Keybinds.remove(this);
                        group.Keybinds.add(indx - 1, this);
                        parent.removeView(model.view);
                        parent.addView(model.view, indx - 1);
                    }
                }else{
                    if(indx<group.Keybinds.size()-1){
                        group.Keybinds.remove(this);
                        group.Keybinds.add(indx + 1, this);
                        parent.removeView(model.view);
                        parent.addView(model.view, indx + 1);
                    }
                }
            };
            ap.Show();
        });
      /*  d.findViewById(R.id.keybind_movedown).setOnClickListener(v->{
            int i=group.Keybinds.indexOf(this);
            if(i<group.Keybinds.size()-1){
                group.Keybinds.remove(this);
                group.Keybinds.add(i+1,this);
                LinearLayout l=((LinearLayout)model.view.getParent());
                l.removeView(this.model.view);
                l.addView(this.model.view,i+1);

            }
        });*/
        d.findViewById(R.id.keybind_sendtogroup).setOnClickListener(v->{
            d.cancel();
            List<KeybindGroup> gs=new ArrayList<>();
            for (KeybindGroup g: Project.Groups) {
                if(g!=group)
                    gs.add(g);
            }
            GroupListProvider  glp=new GroupListProvider(context,"Send Keybind To",gs);
            glp.groupClick=g->{
                this.group.Keybinds.remove(this);
                ((LinearLayout)model.view.getParent()).removeView(model.view);
                g.AddKeybind(this);
            };
            glp.Show();
        });
        d.show();
    }
    //Create a copy of an existing keybind
    public Keybind Clone(Boolean SameName){
        //increment i until the names arent matching
        if(!SameName) {
            int i = 1;
            while (!Project.isKeybindNameAvailable(name + " (" + i + ")"))
                i++;
            //return a copy of the keybind with (i) after the name
        return new Keybind(context,name+" ("+i+")",kb1,kb2,kb3,group);
        }
        return new Keybind(context,name,kb1,kb2,kb3,group);
    }

    public void RebuildView() {
        model=new KeybindViewModel(context,this);
    }
}
