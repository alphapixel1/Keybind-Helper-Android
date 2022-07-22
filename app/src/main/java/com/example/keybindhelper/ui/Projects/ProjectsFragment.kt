package com.example.keybindhelper.ui.Projects

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Dialogs.ButtonDialogProvider
import com.example.keybindhelper.Dialogs.ConfirmDialog
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.RecyclerViewAdapters.ProjectAdapter
import com.example.keybindhelper.cloud.FirebaseDAO
import com.example.keybindhelper.cloud.FirebaseProject
import com.example.keybindhelper.ITaskResponse
import com.example.keybindhelper.Theme.ThemeManager
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dto.Project
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

//import com.example.keybindhelperv3.databinding.FragmentSlideshowBinding

class ProjectsFragment : Fragment() {

    private lateinit var root: View;

    //private var selectedProject: Project?=null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        root =LayoutInflater.from(this.context).inflate(R.layout.fragment_projects, container, false)


        val mainActivity=activity as MainActivity;
        mainActivity.showMenuItems(mainActivity.projectsFragmentActionMenuIds)

        val cloud=mainActivity.Menu?.findItem(R.id.action_cloud)!!;
        if(FirebaseDAO.isUserSignedIn){
            cloud.setIcon(R.drawable.ic_baseline_cloud_24)
            cloud.setOnMenuItemClickListener {
                val snackbar=showSnackBarMessage("Loading Hosted Projects...");
                FirebaseDAO.getUserProjects(object : ITaskResponse<MutableList<FirebaseProject>> {
                    override fun onResponse(task: MutableList<FirebaseProject>) {
                        snackbar.dismiss()
                        if(task.isEmpty()) {
                            showSnackBarMessage("There are no projects currently being hosted.")
                            return;
                        }
                        val names=task.map { it.name };
                        val bdp=ButtonDialogProvider(context!!,"Hosted Projects",names,object: ITaskResponse<String>{
                            //hosted project has been selected now showing download and delete options
                            override fun onResponse(projectName: String) {
                                val subBdp=ButtonDialogProvider(context!!,projectName, mutableListOf("Download","Delete") as List<String>,object: ITaskResponse<String>{
                                    override fun onResponse(result: String) {
                                        if(result=="Download"){
                                            FirebaseDAO.download(projectName, task, object: ITaskResponse<String>{
                                                override fun onResponse(result: String) {
                                                    showSnackBarMessage(result);
                                                    RefreshProjectList();
                                                }
                                            });
                                        }else{
                                            //delete button clicked
                                            val cd=ConfirmDialog(context,"Are you sure you want to delete \"$projectName\"?")
                                            cd.onConfirmed= ConfirmDialog.ConfirmedEvent {
                                                FirebaseDAO.delete(projectName,task,object: ITaskResponse<String>{
                                                    override fun onResponse(result: String) {
                                                        showSnackBarMessage(result);
                                                        RefreshProjectList();
                                                    }

                                                })
                                            }
                                            cd.Show()
                                        }
                                    }
                                });
                                subBdp.show();
                            }

                        })
                        bdp.show()
                    }
                })
                true
            }
            //println(Firebase.storage("gs://"+ FirebaseDAO.currentUser!!.email).app.name)
        }else{
            cloud.setIcon(R.drawable.disabled_cloud_24)
            cloud.setOnMenuItemClickListener {
                showSnackBarMessage("Must be signed in to use cloud features, go to Settings");
                true
            }
        }

        mainActivity.Menu?.findItem(R.id.action_add)?.setOnMenuItemClickListener {
            val pd=PromptDialog(root.context,"New Project Name","","",null);
            val projects= DatabaseManager.db.projects;
            pd.validation= PromptDialog.Validator {
                ValidatorResponse(DatabaseManager.isProjectNameAvailable(projects,it),"A Project Already Exists By That Name")
            }
            pd.confirmedEvent= PromptDialog.ConfirmedEvent {
                val p= Project();
                p.name.value=it;
                CurrentProjectManager.loadProject(p,false)
                p.id=DatabaseManager.db.insert(p)
                openKeybindFragment();
            }
            pd.ShowDialog()
            true;
        };




        val rv= root.findViewById<RecyclerView>(R.id.projects_recycler);
        RefreshProjectList()
        rv.layoutManager=LinearLayoutManager(rv.context)

        val searchEdit=root.findViewById<TextInputEditText>(R.id.projects_search_edit);
        searchEdit.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                RefreshProjectList();
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        )
        applyTheme();
        return root;

    }
    fun openKeybindFragment(){
        NavHostFragment.findNavController(this).navigate(R.id.nav_keybind)
    }
    fun RefreshProjectList():List<Project>{
        val rv= root.findViewById<RecyclerView>(R.id.projects_recycler);
        val projects=DatabaseManager.getOrderedProjects()
        val searchBox=root.findViewById<EditText>(R.id.projects_search_edit);
        if(searchBox.text.length>0) {
            val filteredP= mutableListOf<Project>();
            val search=searchBox.text.toString().lowercase();
            for (project in projects) {
                if(project.name.value!!.lowercase().contains(search))
                filteredP+=(project);
            }
            rv.adapter=ProjectAdapter(filteredP,this);
        }else
            rv.adapter=ProjectAdapter(projects,this);
        return projects;
    }
    private fun showSnackBarMessage(message: String): Snackbar{
        val snack=Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        snack.setAction("Action", null).show()
        return snack
    }

    private fun applyTheme(){
        val cTheme=ThemeManager.CurrentTheme!!;
        val textColor=resources.getColor(cTheme.iconColor);
        root.findViewById<TextView>(R.id.textView4).setTextColor(textColor)
        root.findViewById<TextView>(R.id.textView5).setTextColor(textColor)
    }
}