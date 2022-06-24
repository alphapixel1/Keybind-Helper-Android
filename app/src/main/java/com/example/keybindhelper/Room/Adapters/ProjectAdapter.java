package com.example.keybindhelper.Room.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keybindhelper.Dialogs.ConfirmDialog;
import com.example.keybindhelper.Dialogs.PromptDialog;
import com.example.keybindhelper.Dialogs.ValidatorResponse;
import com.example.keybindhelper.R;
import com.example.keybindhelper.Room.CurrentProject;
import com.example.keybindhelper.Room.DatabaseManager;
import com.example.keybindhelper.Room.Group;
import com.example.keybindhelper.Room.Keybind;
import com.example.keybindhelper.Room.Project;
import com.example.keybindhelper.ui.Projects.ProjectsFragment;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private final List<Project> projectList;
    private final ProjectsFragment fragment;

    public ProjectAdapter(List<Project> projectList, ProjectsFragment fragment){
        this.projectList = projectList;
        this.fragment=fragment;
    }
    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.project_view,parent,false);
        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project p=projectList.get(position);

        ((TextView)holder.itemView.findViewById(R.id.project_name)).setText(p.name);
        Calendar cal = Calendar.getInstance();
        cal.setTime(p.lastAccessed);
        int day= cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR)%100;
        String min=cal.get(Calendar.MINUTE)+"";
        if(min.length()==1)
            min="0"+min;
        int hour=cal.get(Calendar.HOUR);
        String ampm=cal.get(Calendar.AM_PM)==0? "am":"pm";

        ((TextView)holder.itemView.findViewById(R.id.project_last_accessed)).setText(
                MessageFormat.format("{0}/{1}/{2} {3}:{4}{5}", month,day,year,hour,min,ampm)
        );
        //view clicked
        holder.itemView.findViewById(R.id.project_view_btn).setOnClickListener(v-> {
            Context context=holder.itemView.getContext();
            Dialog d =new Dialog(context);
            d.setContentView(R.layout.project_more_menu);
            ((TextView)d.findViewById(R.id.project_menu_name)).setText(p.name);

            //delete project
            d.findViewById(R.id.project_menu_delete_btn).setOnClickListener (z->{
                ConfirmDialog cd=new ConfirmDialog(context,"Are you sure you want to delete "+p.name+"?");
                cd.onConfirmed= () -> {
                    DatabaseManager.db.delete(p);
                    if(CurrentProject.CurrentProject.id==p.id) {
                        CurrentProject.loadFirstProject();
                    }
                    fragment.RefreshProjectList();

                    d.cancel();
                };
                cd.Show();

            });
            //open project
            d.findViewById(R.id.project_menu_open_btn).setOnClickListener (z->{
                CurrentProject.loadProject(p, true);
                fragment.openKeybindFragment();
                d.cancel();
               // openKeybindFragment();
            });
            //rename project
            d.findViewById(R.id.projefct_menu_rename_btn).setOnClickListener (z-> {
                d.cancel();
                PromptDialog pd = new PromptDialog(context, "Rename Project", null, p.name,null);
                pd.validation = text -> new ValidatorResponse(DatabaseManager.isProjectNameAvailable(text), "A Project Already Exists By That Name");
                pd.confirmedEvent = text-> {
                    if(Objects.equals(CurrentProject.CurrentProject.name, p.name))
                        CurrentProject.CurrentProject.name=text;
                    p.name = text;
                    p.updateLastAccessed();
                    DatabaseManager.db.update(p);
                    notifyItemChanged(position);

                    //notifyDataSetChanged();
                    };
                pd.ShowDialog();
            });

            //clone project
            d.findViewById(R.id.project_menu_copy_btn).setOnClickListener(z->{
                int i = 1;
                while (!CurrentProject.isProjectNameAvailable(projectList,p.name+" ("+i+")"))
                    i++;
                Project np=new Project();
                np.name=p.name+" ("+i+")";
                np.updateLastAccessed();

                np.id=DatabaseManager.db.insert(np);

                for (Group g:DatabaseManager.db.getProjectGroups(p.id)){
                    Group ng= new Group();
                    ng.name=g.name;
                    ng.index=g.index;
                    ng.projectID=np.id;
                    ng.id=DatabaseManager.db.insert(ng);
                    for (Keybind k: DatabaseManager.db.getGroupKeybinds(g.id)){
                        Keybind nk=new Keybind();
                        nk.groupID=ng.id;
                        nk.name= k.name;
                        nk.kb1=k.kb1;
                        nk.kb2=k.kb2;
                        nk.kb3=k.kb3;
                        nk.index=k.index;
                        DatabaseManager.db.insert(nk);
                    }

                }
                CurrentProject.loadProject(np,false);
                System.out.println("DB COPIED GROUP COUNT: "+CurrentProject.Groups.size());
                fragment.RefreshProjectList();
            });
            d.show();
        });

    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        public ProjectViewHolder(View itemView){
            super(itemView);
        }
    }
}
