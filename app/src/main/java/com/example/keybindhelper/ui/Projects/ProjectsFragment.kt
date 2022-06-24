package com.example.keybindhelper.ui.Projects

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.Room.Adapters.ProjectAdapter
import com.example.keybindhelper.Room.CurrentProject
import com.example.keybindhelper.Room.DatabaseManager
import com.example.keybindhelper.Room.Project
import com.example.keybindhelper.ui.keybind.KeybindFragment
import com.google.android.material.textfield.TextInputEditText

//import com.example.keybindhelperv3.databinding.FragmentSlideshowBinding

class ProjectsFragment : Fragment() {

    private lateinit var root: View;

    // This property is only valid between onCreateView and
    // onDestroyView.
   // private val binding get() = _binding!!
    private var selectedProject: Project?=null;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(ProjectsViewModel::class.java)
            root =LayoutInflater.from(this.context).inflate(R.layout.fragment_projects, container, false)
      //_binding = LayoutInflater.inflate(inflater, container, false)


        val mainActivity=activity as MainActivity;
        mainActivity.showMenuItems(mainActivity.projectsFragmentActionMenuIds)

        mainActivity.Menu?.findItem(R.id.action_add)?.setOnMenuItemClickListener {
            val pd=PromptDialog(root.context,"New Project Name","","",null);
            pd.validation= PromptDialog.Validator {

                ValidatorResponse(DatabaseManager.isProjectNameAvailable(it),"A Project Already Exists By That Name")
            }
            pd.confirmedEvent= PromptDialog.ConfirmedEvent {
                val p=Project();
                p.name=it
                CurrentProject.loadProject(p,false)
                DatabaseManager.db.insert(p)
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


    override fun onDestroyView() {
        super.onDestroyView()

    }
}