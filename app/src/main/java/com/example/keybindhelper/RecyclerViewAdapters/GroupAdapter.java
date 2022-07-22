package com.example.keybindhelper.RecyclerViewAdapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keybindhelper.Dialogs.ArrowProvider;
import com.example.keybindhelper.Dialogs.GroupListProvider;
import com.example.keybindhelper.Dialogs.PromptDialog;
import com.example.keybindhelper.Dialogs.ValidatorResponse;
import com.example.keybindhelper.R;
import com.example.keybindhelper.Theme.Theme;
import com.example.keybindhelper.Theme.ThemeManager;
import com.example.keybindhelper.dao.CurrentProjectManager;
import com.example.keybindhelper.dao.DatabaseManager;
import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;

    public GroupAdapter(List<Group> groupList){
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_layout,parent,false);
        return new GroupViewHolder(v);
    }

    private void applyTheme(View view){
/*
        Theme theme=ThemeManager.INSTANCE.getCurrentTheme();
        if(theme!=null) {
            view.findViewById(R.id.group_background).setBackgroundColor(android.content.res.Resources.getColor(theme.getKeybindBackgroundColor()));
            view.findViewById(R.id.group_background).setBackgroundColor(theme.getKeybindBackgroundColor());
            ((TextView) view.findViewById(R.id.group_name_button)).setTextColor(theme.getIconColor());
            view.findViewById(R.id.group_add_button).setBackgroundColor(theme.getIconColor());
            view.findViewById(R.id.group_more_button).setBackgroundColor(theme.getIconColor());
        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group=groupList.get(position);
        applyTheme(holder.itemView);
        group.currentAdapter=this;
        Context context=holder.itemView.getContext();

        RecyclerView rv= holder.itemView.findViewById(R.id.keybind_zone);
        rv.setAdapter(new KeybindAdapter(group.keybinds));
        rv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        Button nameBtn=holder.itemView.findViewById(R.id.group_name_button);

        group.name.observe((LifecycleOwner) context,nameBtn::setText);

        nameBtn.setText(group.name.getValue());
        nameBtn.setOnClickListener(v->{
            PromptDialog pd= new PromptDialog(
                    holder.itemView.getContext(),
                    "Rename Group",
                    "",
                    group.name.getValue(),
                    null
            );
            pd.validation= text -> new ValidatorResponse(
                    text.equals(group.name) || CurrentProjectManager.CurrentProject.isGroupNameAvailable(text),
                    "Name Has Already Been Taken");
            pd.confirmedEvent= n->{
                group.name.setValue(n);

                DatabaseManager.db.update(group);
            };
            pd.ShowDialog();
        });

        holder.itemView.findViewById(R.id.group_add_button).setOnClickListener(v->{
            group.AddKeybind();

            rv.getAdapter().notifyItemChanged(group.keybinds.size()-1);
            if(rv.getVisibility()!=View.VISIBLE)
                rv.setVisibility(View.VISIBLE);
        });

        holder.itemView.findViewById(R.id.group_more_button).setOnClickListener(z->{
            Dialog d=new Dialog(context);
            d.setContentView(R.layout.group_menu);
            ((TextView)d.findViewById(R.id.group_name)).setText(group.name.getValue());
            d.findViewById(R.id.group_clear_keybinds).setOnClickListener(v->{
                DatabaseManager.db.deleteGroupKeybinds(group.id);
                group.keybinds.clear();
                try {
                    rv.getAdapter().notifyDataSetChanged();
                }catch (Exception e){

                }
                d.cancel();
            });
            d.findViewById(R.id.group_delete).setOnClickListener(v->{
                notifyItemRemoved(CurrentProjectManager.CurrentProject.Groups.indexOf(group));
                CurrentProjectManager.CurrentProject.Groups.remove(group);
                DatabaseManager.db.deleteGroup(group.id);

                d.cancel();
            });
            d.findViewById(R.id.group_showhide).setOnClickListener(v->{
                rv.setVisibility(rv.getVisibility()==View.VISIBLE? View.GONE:View.VISIBLE);
                d.cancel();
            });
            d.findViewById(R.id.group_copy).setOnClickListener(v->{
                Group g=group.Clone();
                notifyItemInserted(g.index);
                d.cancel();
            });
            d.findViewById(R.id.group_dissolve).setOnClickListener(v->{
                List<Group> groups= new ArrayList<>();
                for (Group g: CurrentProjectManager.CurrentProject.Groups) {
                    if(g!=group)
                        groups.add(g);
                }
                GroupListProvider glp=new GroupListProvider(context,"Dissolve Group Into",groups);
                d.cancel();
                glp.groupClick=newGroup->{
                    for (Keybind kb:group.keybinds.toArray(new Keybind[0])) {
                        newGroup.AddKeybind(kb,false);
                    }
                    newGroup.currentAdapter.notifyDataSetChanged();
                    CurrentProjectManager.CurrentProject.Groups.remove(group);
                    DatabaseManager.db.deleteGroup(group.id);
                    CurrentProjectManager.CurrentProject.updateGroupIndexes();
                };
                glp.Show();
            });
            d.findViewById(R.id.group_move).setOnClickListener(v->{
                d.cancel();
                ArrowProvider ap=new ArrowProvider(context);
                ap.directionClicked=isUp -> {
                    int indx= CurrentProjectManager.CurrentProject.Groups.indexOf(group);
                    System.out.println(indx);
                    if(isUp){
                        if(indx>0) {
                            CurrentProjectManager.CurrentProject.MoveGroupUpDown(group,-1);
                            notifyItemMoved(indx,indx-1);
                        }
                    }else if(indx< CurrentProjectManager.CurrentProject.Groups.size()-1){
                            CurrentProjectManager.CurrentProject.MoveGroupUpDown(group,1);
                            notifyItemMoved(indx,indx+1);
                    }
                };
                ap.Show();
            });
            d.show();

        });



    }

    @Override
    public int getItemCount() {
        if(groupList==null)
            return 0;
        return groupList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        public GroupViewHolder(View itemView){
            super(itemView);

        }
    }
}
