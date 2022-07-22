package com.example.keybindhelper.RecyclerViewAdapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keybindhelper.Dialogs.ButtonDialogProvider;
import com.example.keybindhelper.Dialogs.ConfirmDialog;
import com.example.keybindhelper.Dialogs.PromptDialog;
import com.example.keybindhelper.Dialogs.ValidatorResponse;
import com.example.keybindhelper.ITaskResponse;
import com.example.keybindhelper.R;
import com.example.keybindhelper.cloud.FirebaseDAO;
import com.example.keybindhelper.dao.CurrentProjectManager;
import com.example.keybindhelper.dao.DatabaseManager;
import com.example.keybindhelper.dto.Group;
import com.example.keybindhelper.dto.Keybind;
import com.example.keybindhelper.dto.Project;
import com.example.keybindhelper.ui.Projects.ProjectsFragment;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.Arrays;
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
    private void showSnackBarMessage(String message){
        Snackbar.make(fragment.getView(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project p=projectList.get(position);

        LifecycleOwner lifecycleOwner=(LifecycleOwner)holder.itemView.getContext();
        p.name.observe(lifecycleOwner,((TextView)holder.itemView.findViewById(R.id.project_name))::setText);
        //Formating to month/day/year hour:min(am/pm)
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
        Context context=holder.itemView.getContext();
        //view clicked
        holder.itemView.findViewById(R.id.project_view_btn).setOnClickListener(v-> {

            ButtonDialogProvider bdp=new ButtonDialogProvider(context,p.name.getValue(), Arrays.asList("OPEN", "COPY","RENAME","DELETE","HOST"), result -> {
                switch (result){
                    case "OPEN":
                        CurrentProjectManager.loadProject(p, true);
                        fragment.openKeybindFragment();
                        break;
                    case "COPY":
                        Project np=new Project();
                        np.name.setValue(DatabaseManager.getFirstAvailableProjectName(p.name.getValue()));
                        np.updateLastAccessed();
                        np.id=DatabaseManager.db.insert(np);

                        for (Group g:DatabaseManager.db.getProjectGroups(p.id)){
                            Group ng= new Group(g.name.getValue(),np.id);
                            ng.index=g.index;
                            ng.id=DatabaseManager.db.insert(ng);
                            for (Keybind k: DatabaseManager.db.getGroupKeybinds(g.id)){
                                Keybind nk=new Keybind(ng.id,k.name.getValue(),k.kb1.getValue(), k.kb2.getValue(), k.kb3.getValue());
                                nk.index=k.index;
                                DatabaseManager.db.insert(nk);
                            }

                        }
                        showSnackBarMessage("Copied as "+np.name.getValue()+"!");
                        CurrentProjectManager.loadProject(np,false);
                        //System.out.println("DB COPIED GROUP COUNT: "+ CurrentProjectManager.CurrentProject.Groups.size());
                        fragment.RefreshProjectList();
                        break;
                    case "RENAME":
                        List<Project> projects=DatabaseManager.db.getProjects();
                        PromptDialog pd = new PromptDialog(context, "Rename Project", null, p.name.getValue(),null);
                        pd.validation = text -> new ValidatorResponse(DatabaseManager.isProjectNameAvailable(projects,text), "A Project Already Exists By That Name");
                        pd.confirmedEvent = text-> {
                            if(Objects.equals(CurrentProjectManager.CurrentProject.name.getValue(), p.name.getValue()))
                                CurrentProjectManager.CurrentProject.name.setValue(text);
                            p.name.setValue(text);
                            p.updateLastAccessed();
                            DatabaseManager.db.update(p);
                            notifyItemChanged(position);
                            showSnackBarMessage("Renamed to \'"+text+"\'!");

                        };
                        pd.ShowDialog();
                        break;
                    case "DELETE":
                        ConfirmDialog cd=new ConfirmDialog(context,"Are you sure you want to delete "+p.name.getValue()+"?");
                        cd.onConfirmed= () -> {
                            DatabaseManager.db.delete(p);
                            if(CurrentProjectManager.CurrentProject.id==p.id) {
                                CurrentProjectManager.loadFirstProject();
                            }
                            fragment.RefreshProjectList();
                            showSnackBarMessage(p.name.getValue()+" Deleted!");
                        };
                        cd.Show();
                        break;
                    case "HOST":
                        if(FirebaseDAO.INSTANCE.isUserSignedIn()){
                            FirebaseDAO.INSTANCE.getUserProjects(fireProjects -> {
                                int max=FirebaseDAO.INSTANCE.getMaxProjects();
                                if(max<fireProjects.size()+1){
                                    showSnackBarMessage("Max Cloud Projects Reached ("+max+")");
                                }else{
                                    String name=FirebaseDAO.INSTANCE.getUninqueProjectName(Objects.requireNonNull(p.name.getValue()),fireProjects);
                                    FirebaseDAO.INSTANCE.addProject(p,name,res->{
                                        if(res)
                                            showSnackBarMessage("Successfully Hosted Project as \""+name +"\"");
                                        else
                                            showSnackBarMessage("Unable To Host Project");
                                    });
                                }
                            });
                        }else{
                            showSnackBarMessage("User must be signed in to Host files, Sign in with Settings");
                        }
                    break;

                }
            });
            bdp.show();
/*MOVED TO BUTTONDIALOGPROVIDER
            Dialog d =new Dialog(context);
            d.setContentView(R.layout.project_more_menu);
            ((TextView)d.findViewById(R.id.project_menu_name)).setText(p.name.getValue());


            //delete project
            d.findViewById(R.id.project_menu_delete_btn).setOnClickListener (z->{
                ConfirmDialog cd=new ConfirmDialog(context,"Are you sure you want to delete "+p.name.getValue()+"?");
                cd.onConfirmed= () -> {
                    DatabaseManager.db.delete(p);
                    if(CurrentProjectManager.CurrentProject.id==p.id) {
                        CurrentProjectManager.loadFirstProject();
                    }
                    fragment.RefreshProjectList();
                    showSnackBarMessage(p.name.getValue()+" Deleted!");
                    d.cancel();
                };
                cd.Show();

            });
            //open project
            d.findViewById(R.id.project_menu_open_btn).setOnClickListener (z->{
                CurrentProjectManager.loadProject(p, true);
                fragment.openKeybindFragment();
                d.cancel();
               // openKeybindFragment();
            });
            //rename project
            d.findViewById(R.id.projefct_menu_rename_btn).setOnClickListener (z-> {
                d.cancel();
                List<Project> projects=DatabaseManager.db.getProjects();
                PromptDialog pd = new PromptDialog(context, "Rename Project", null, p.name.getValue(),null);
                pd.validation = text -> new ValidatorResponse(DatabaseManager.isProjectNameAvailable(projects,text), "A Project Already Exists By That Name");
                pd.confirmedEvent = text-> {
                    if(Objects.equals(CurrentProjectManager.CurrentProject.name.getValue(), p.name.getValue()))
                        CurrentProjectManager.CurrentProject.name.setValue(text);
                    p.name.setValue(text);
                    p.updateLastAccessed();
                    DatabaseManager.db.update(p);
                    notifyItemChanged(position);
                    showSnackBarMessage("Renamed to \'"+text+"\'!");
                    //notifyDataSetChanged();
                    };
                pd.ShowDialog();
            });

            //clone project
            d.findViewById(R.id.project_menu_copy_btn).setOnClickListener(z->{
                Project np=new Project();
                np.name.setValue(DatabaseManager.getFirstAvailableProjectName(p.name.getValue()));
                np.updateLastAccessed();
                np.id=DatabaseManager.db.insert(np);

                for (Group g:DatabaseManager.db.getProjectGroups(p.id)){
                    Group ng= new Group(g.name.getValue(),np.id);
                    ng.index=g.index;
                    ng.id=DatabaseManager.db.insert(ng);
                    for (Keybind k: DatabaseManager.db.getGroupKeybinds(g.id)){
                        Keybind nk=new Keybind(ng.id,k.name.getValue(),k.kb1.getValue(), k.kb2.getValue(), k.kb3.getValue());
                        nk.index=k.index;
                        DatabaseManager.db.insert(nk);
                    }

                }
                showSnackBarMessage("Copied as "+np.name.getValue()+"!");
                CurrentProjectManager.loadProject(np,false);
                //System.out.println("DB COPIED GROUP COUNT: "+ CurrentProjectManager.CurrentProject.Groups.size());
                fragment.RefreshProjectList();
                d.cancel();
            });

            //host Project
            Button hostBtn=d.findViewById(R.id.project_menu_host_btn);
            if(FirebaseDAO.INSTANCE.isUserSignedIn()){
                hostBtn.setOnClickListener(v1 ->
                    {
                        d.cancel();
                        FirebaseDAO.INSTANCE.getUserProjects(fireProjects -> {
                            int max=FirebaseDAO.INSTANCE.getMaxProjects();
                            if(max<fireProjects.size()+1){
                                showSnackBarMessage("Max Cloud Projects Reached ("+max+")");
                            }else{
                                String name=FirebaseDAO.INSTANCE.getUninqueProjectName(Objects.requireNonNull(p.name.getValue()),fireProjects);
                                FirebaseDAO.INSTANCE.addProject(p,name,result->{
                                    if(result)
                                        showSnackBarMessage("Successfully Hosted Project as \""+name +"\"");
                                    else
                                        showSnackBarMessage("Unable To Host Project");
                                });
                            }
                        });
                    }

                );
            }else{
                hostBtn.setOnClickListener(v1 -> showSnackBarMessage("User must be signed in to Host files, Sign in with Settings"));
            }
            d.show();*/
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
