package com.example.keybindhelper.Adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Adapters.ProjectAdapter.ProjectViewHolder
import com.example.keybindhelper.Dialogs.ConfirmDialog
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.R
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.CurrentProjectManager.isProjectNameAvailable
import com.example.keybindhelper.dao.CurrentProjectManager.loadFirstProject
import com.example.keybindhelper.dao.CurrentProjectManager.loadProject
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dao.DatabaseManager.isProjectNameAvailable
import com.example.keybindhelper.dto.Group
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dto.Project
import com.example.keybindhelper.ui.projects.ProjectsFragment
import java.text.MessageFormat
import java.util.*

class ProjectAdapter(
    private val projectList: MutableList<Project>,
    private val fragment: ProjectsFragment
) : RecyclerView.Adapter<ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.project_view, parent, false)
        return ProjectViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val p = projectList[position]
        (holder.itemView.findViewById<View>(R.id.project_name) as TextView).text = p.name
        val cal = Calendar.getInstance()
        cal.time = p.lastAccessed!!
        val day = cal[Calendar.DAY_OF_MONTH]
        val month = cal[Calendar.MONTH]
        val year = cal[Calendar.YEAR] % 100
        var min = cal[Calendar.MINUTE].toString() + ""
        if (min.length == 1) min = "0$min"
        val hour = cal[Calendar.HOUR]
        val ampm = if (cal[Calendar.AM_PM] == 0) "am" else "pm"
        (holder.itemView.findViewById<View>(R.id.project_last_accessed) as TextView).text =
            MessageFormat.format("{0}/{1}/{2} {3}:{4}{5}", month, day, year, hour, min, ampm)
        //view clicked
        holder.itemView.findViewById<View>(R.id.project_view_btn).setOnClickListener { v: View? ->
            val context = holder.itemView.context
            val d = Dialog(context)
            d.setContentView(R.layout.project_more_menu)
            (d.findViewById<View>(R.id.project_menu_name) as TextView).text = p.name

            //delete project
            d.findViewById<View>(R.id.project_menu_delete_btn).setOnClickListener { z: View? ->
                val cd = ConfirmDialog(context, "Are you sure you want to delete " + p.name + "?")
                cd.onConfirmed= object : ConfirmDialog.ConfirmedEvent{
                    override fun onConfirmed() {
                        DatabaseManager.db!!.delete(p)
                        if (CurrentProjectManager.CurrentProject!!.id == p.id) {
                            loadFirstProject()
                        }
                        fragment.RefreshProjectList()
                        d.cancel()
                    }
                }
                cd.Show()
            }
            //open project
            d.findViewById<View>(R.id.project_menu_open_btn).setOnClickListener { z: View? ->
                loadProject(p, true)
                fragment.openKeybindFragment()
                d.cancel()
            }
            //rename project
            d.findViewById<View>(R.id.project_menu_rename_btn).setOnClickListener { z: View? ->
                d.cancel()
                val pd = PromptDialog(context, "Rename Project", null, p.name, null)
                pd.validation = object : PromptDialog.Validator{
                    override fun Validate(text: String?): ValidatorResponse = ValidatorResponse(
                        isProjectNameAvailable(
                            text!!), "A Project Already Exists By That Name")
                }
                pd.confirmedEvent=object : PromptDialog.ConfirmedEvent{
                    override fun onConfirmed(text: String?) {
                        if (CurrentProjectManager.CurrentProject!!.name == p.name) CurrentProjectManager.CurrentProject!!.name =
                            text
                        p.name = text
                        p.updateLastAccessed()
                        DatabaseManager.db!!.update(p)
                        notifyItemChanged(position)
                    }
                }

                pd.ShowDialog()
            }

            //clone project
            d.findViewById<View>(R.id.project_menu_copy_btn).setOnClickListener { z: View? ->
                var i = 1
                while (!isProjectNameAvailable(projectList, p.name + " (" + i + ")")) i++
                val np = Project()
                np.name = p.name + " (" + i + ")"
                np.updateLastAccessed()
                np.id = DatabaseManager.db!!.insert(np)
                for (g in DatabaseManager.db!!.getProjectGroups(p.id)!!) {
                    val ng = Group()
                    ng.name = g.name
                    ng.index = g.index
                    ng.projectID = np.id
                    ng.id = DatabaseManager.db!!.insert(ng)
                    for (k in DatabaseManager.db!!.getGroupKeybinds(g.id)) {
                        val nk = Keybind()
                        nk.groupID = ng.id
                        nk.name = k.name
                        nk.kb1 = k.kb1
                        nk.kb2 = k.kb2
                        nk.kb3 = k.kb3
                        nk.index = k.index
                        DatabaseManager.db!!.insert(nk)
                    }
                }
                loadProject(np, false)
                println("DB COPIED GROUP COUNT: " + CurrentProjectManager.Groups!!.size)
                fragment.RefreshProjectList()
            }
            d.show()
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    inner class ProjectViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!)
}