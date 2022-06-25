package com.example.keybindhelper.ui.Projects

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.RecyclerViewAdapters.ProjectAdapter
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dto.Project
import com.google.android.material.textfield.TextInputEditText

//import com.example.keybindhelperv3.databinding.FragmentSlideshowBinding

class ProjectsFragment : Fragment() {

    private lateinit var root: View;

    //private var selectedProject: Project?=null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        root =LayoutInflater.from(this.context).inflate(R.layout.fragment_projects, container, false)


        val mainActivity=activity as MainActivity;
        mainActivity.showMenuItems(mainActivity.projectsFragmentActionMenuIds)

        mainActivity.Menu?.findItem(R.id.action_add)?.setOnMenuItemClickListener {
            val pd=PromptDialog(root.context,"New Project Name","","",null);
            val projects= DatabaseManager.db.getProjects();
            pd.validation= PromptDialog.Validator {
                ValidatorResponse(DatabaseManager.isProjectNameAvailable(projects,it),"A Project Already Exists By That Name")
            }
            pd.confirmedEvent= PromptDialog.ConfirmedEvent {
                val p= Project();
                p.name=it
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

            override fun afterTextChanged(s: Editable?) {

            }
        }
        )

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
                if(project.name.lowercase().contains(search))
                filteredP+=(project);
            }
            rv.adapter=ProjectAdapter(filteredP,this);
        }else
            rv.adapter=ProjectAdapter(projects,this);
        return projects;
    }



}