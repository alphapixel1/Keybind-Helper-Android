package com.example.keybindhelper.RecyclerViewAdapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import com.example.keybindhelper.cloud.FirebaseDAO.isUserSignedIn
import com.example.keybindhelper.cloud.FirebaseDAO.getUserProjects
import com.example.keybindhelper.dto.Project
import com.example.keybindhelper.ui.Projects.ProjectsFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.RecyclerViewAdapters.ProjectAdapter.ProjectViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.example.keybindhelper.R
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.LifecycleOwner
import android.widget.TextView
import com.example.keybindhelper.Dialogs.ButtonDialogProvider
import com.example.keybindhelper.ITaskResponse
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.Dialogs.ConfirmDialog
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.Theme.ThemeManager
import com.example.keybindhelper.cloud.FirebaseDAO
import com.example.keybindhelper.cloud.FirebaseProject
import com.example.keybindhelper.dto.Group
import java.text.MessageFormat
import java.util.*

class ProjectAdapter(
    private val projectList: List<Project>,
    private val fragment: ProjectsFragment
) : RecyclerView.Adapter<ProjectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.project_view, parent, false)
        return ProjectViewHolder(v)
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(fragment.view!!, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }
    private fun applyTheme(view: View,position: Int) {
        val resources = (view.context as MainActivity).resources;
        val theme = ThemeManager.CurrentTheme!!;
        val textColor=resources.getColor(theme.iconColor);
        view.findViewById<TextView>(R.id.project_name).setTextColor(textColor);
        view.findViewById<TextView>(R.id.project_last_accessed).setTextColor(textColor);
        val background=if(position%2==0) theme.keybindBackgroundColor else theme.offsetKeybindBackgroundColor
        view.findViewById<Button>(R.id.project_view_btn).backgroundTintList= ColorStateList.valueOf(resources.getColor(background));
    }
    override fun onBindViewHolder(holder: ProjectViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val p = projectList[position]
        applyTheme(holder.itemView,position);
        val lifecycleOwner = holder.itemView.context as LifecycleOwner
        p.name.observe(lifecycleOwner) { text: String? ->
            (holder.itemView.findViewById<View>(R.id.project_name) as TextView).text = text
        }
        //Formating to month/day/year hour:min(am/pm)
        val cal = Calendar.getInstance()
        cal.time = p.lastAccessed
        val day = cal[Calendar.DAY_OF_MONTH]
        val month = cal[Calendar.MONTH]
        val year = cal[Calendar.YEAR] % 100
        var min = cal[Calendar.MINUTE].toString() + ""
        if (min.length == 1) min = "0$min"
        val hour = cal[Calendar.HOUR]
        val ampm = if (cal[Calendar.AM_PM] == 0) "am" else "pm"
        val lastAccessed =
            holder.itemView.findViewById<View>(R.id.project_last_accessed) as TextView
        lastAccessed.text =
            MessageFormat.format("{0}/{1}/{2} {3}:{4}{5}", month, day, year, hour, min, ampm)
        val context = holder.itemView.context
        //view clicked
        holder.itemView.findViewById<View>(R.id.project_view_btn).setOnClickListener { v: View? ->
            val bdp = ButtonDialogProvider(context,
                p.name.value!!,
                Arrays.asList("OPEN", "COPY", "RENAME", "DELETE", "HOST"),
                object: ITaskResponse<String> {
                    override fun onResponse(result: String) {
                        when (result) {
                            "OPEN" -> {
                                CurrentProjectManager.loadProject(p, true)
                                fragment.openKeybindFragment()
                            }
                            "COPY" -> {
                                val np = Project()
                                np.name.setValue(DatabaseManager.getFirstAvailableProjectName(p.name.value))
                                np.updateLastAccessed()
                                np.id = DatabaseManager.db.insert(np)
                                for (g in DatabaseManager.db.getProjectGroups(p.id)) {
                                    val ng = Group(g.name.value, np.id)
                                    ng.index = g.index
                                    ng.id = DatabaseManager.db.insert(ng)
                                    for (k in DatabaseManager.db.getGroupKeybinds(g.id)) {
                                        val nk = Keybind(ng.id,
                                            k.name.value,
                                            k.kb1.value,
                                            k.kb2.value,
                                            k.kb3.value)
                                        nk.index = k.index
                                        DatabaseManager.db.insert(nk)
                                    }
                                }
                                showSnackBarMessage("Copied as " + np.name.value + "!")
                                CurrentProjectManager.loadProject(np, false)
                                //System.out.println("DB COPIED GROUP COUNT: "+ CurrentProjectManager.CurrentProject.Groups.size());
                                fragment.RefreshProjectList()
                            }
                            "RENAME" -> {
                                val projects = DatabaseManager.db.projects
                                val pd =
                                    PromptDialog(context, "Rename Project", null, p.name.value, null)
                                pd.validation = PromptDialog.Validator { text: String? ->
                                    ValidatorResponse(DatabaseManager.isProjectNameAvailable(projects,
                                        text),
                                        "A Project Already Exists By That Name")
                                }
                                pd.confirmedEvent = PromptDialog.ConfirmedEvent { text: String ->
                                    if (CurrentProjectManager.CurrentProject.name.value == p.name.value) CurrentProjectManager.CurrentProject.name.setValue(
                                        text)
                                    p.name.setValue(text)
                                    p.updateLastAccessed()
                                    DatabaseManager.db.update(p)
                                    notifyItemChanged(position)
                                    showSnackBarMessage("Renamed to \'$text\'!")
                                }
                                pd.ShowDialog()
                            }
                            "DELETE" -> {
                                val cd = ConfirmDialog(context,
                                    "Are you sure you want to delete " + p.name.value + "?")
                                cd.onConfirmed = ConfirmDialog.ConfirmedEvent {
                                    DatabaseManager.db.delete(p)
                                    if (CurrentProjectManager.CurrentProject.id == p.id) {
                                        CurrentProjectManager.loadFirstProject()
                                    }
                                    fragment.RefreshProjectList()
                                    showSnackBarMessage(p.name.value.toString() + " Deleted!")
                                }
                                cd.Show()
                            }
                            "HOST" -> if (isUserSignedIn) {
                                getUserProjects(object: ITaskResponse<MutableList<FirebaseProject>> {
                                    override fun onResponse(fireProjects: MutableList<FirebaseProject>){
                                        val max = FirebaseDAO.maxProjects
                                        if (max < fireProjects.size + 1) {
                                            showSnackBarMessage("Max Cloud Projects Reached ($max)")
                                        } else {
                                            val name = FirebaseDAO.getUninqueProjectName(p.name.value!!,
                                                fireProjects)
                                            FirebaseDAO.addProject(p,
                                                name,
                                                object : ITaskResponse<Boolean> {
                                                    override fun onResponse(res: Boolean) {
                                                        if (res) showSnackBarMessage(
                                                            "Successfully Hosted Project as \"$name\"") else showSnackBarMessage(
                                                            "Unable To Host Project")
                                                    }
                                                })
                                        }
                                    }
                                })
                            } else {
                                showSnackBarMessage("User must be signed in to Host files, Sign in with Settings")
                            }
                        }
                    }
                })
            bdp.show()
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    inner class ProjectViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!)
}