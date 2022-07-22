package com.example.keybindhelper.RecyclerViewAdapters

import android.app.Dialog
import android.content.res.ColorStateList

import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.RecyclerViewAdapters.GroupAdapter.GroupViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import com.example.keybindhelper.R
import com.example.keybindhelper.RecyclerViewAdapters.KeybindAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.LifecycleOwner
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.keybindhelper.Dialogs.GroupListProvider
import com.example.keybindhelper.Dialogs.GroupListProvider.GroupClick
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.Dialogs.ArrowProvider
import com.example.keybindhelper.Dialogs.ArrowProvider.DirectionClicked
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.Theme.Theme
import com.example.keybindhelper.Theme.ThemeManager
import com.example.keybindhelper.dto.Group
import java.lang.Exception
import java.util.ArrayList



class GroupAdapter(private val groupList: List<Group>?) : RecyclerView.Adapter<GroupViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.group_layout, parent, false)
        return GroupViewHolder(v)
    }

    private fun applyTheme(view: View) {
        val resources=(view.context as MainActivity).resources;
        val theme=ThemeManager.CurrentTheme!!;

        view.findViewById<CardView>(R.id.group_card).backgroundTintList= ColorStateList.valueOf(resources.getColor(theme.keybindBackgroundColor))

        val iconTint=ColorStateList.valueOf(resources.getColor(theme.iconColor));
        view.findViewById<TextView>(R.id.group_name_button).setTextColor(iconTint)
        view.findViewById<ImageButton>(R.id.group_add_button).imageTintList= iconTint
        view.findViewById<ImageButton>(R.id.group_more_button).imageTintList= iconTint

        view.findViewById<LinearLayout>(R.id.group_header_background).backgroundTintList= ColorStateList.valueOf(resources.getColor(theme.groupHeaderColor))

    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList!![position]
        applyTheme(holder.itemView)
        group.currentAdapter = this
        val context = holder.itemView.context
        val rv = holder.itemView.findViewById<RecyclerView>(R.id.keybind_zone)
        rv.adapter = KeybindAdapter(group.keybinds)
        rv.layoutManager = LinearLayoutManager(holder.itemView.context)
        val nameBtn = holder.itemView.findViewById<Button>(R.id.group_name_button)
        group.name.observe((context as LifecycleOwner)) { text: String? -> nameBtn.text = text }
        nameBtn.text = group.name.value
        nameBtn.setOnClickListener { v: View? ->
            val pd = PromptDialog(
                holder.itemView.context,
                "Rename Group",
                "",
                group.name.value,
                null
            )
            pd.validation = PromptDialog.Validator { text: String ->
                ValidatorResponse(
                    text == group.name.value || CurrentProjectManager.CurrentProject.isGroupNameAvailable(
                        text),
                    "Name Has Already Been Taken")
            }
            pd.confirmedEvent = PromptDialog.ConfirmedEvent { n: String? ->
                group.name.value = n
                DatabaseManager.db.update(group)
            }
            pd.ShowDialog()
        }
        holder.itemView.findViewById<View>(R.id.group_add_button).setOnClickListener { v: View? ->
            group.AddKeybind()
            rv.adapter!!.notifyItemChanged(group.keybinds.size - 1)
            if (rv.visibility != View.VISIBLE) rv.visibility = View.VISIBLE
        }
        holder.itemView.findViewById<View>(R.id.group_more_button).setOnClickListener { z: View? ->
            val d = Dialog(context)
            d.setContentView(R.layout.group_menu)
            (d.findViewById<View>(R.id.group_name) as TextView).text = group.name.value
            d.findViewById<View>(R.id.group_clear_keybinds).setOnClickListener { v: View? ->
                DatabaseManager.db.deleteGroupKeybinds(group.id)
                group.keybinds.clear()
                try {
                    rv.adapter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                }
                d.cancel()
            }
            d.findViewById<View>(R.id.group_delete).setOnClickListener { v: View? ->
                notifyItemRemoved(CurrentProjectManager.CurrentProject.Groups.indexOf(group))
                CurrentProjectManager.CurrentProject.Groups.remove(group)
                DatabaseManager.db.deleteGroup(group.id)
                d.cancel()
            }
            d.findViewById<View>(R.id.group_showhide).setOnClickListener { v: View? ->
                rv.visibility = if (rv.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                d.cancel()
            }
            d.findViewById<View>(R.id.group_copy).setOnClickListener { v: View? ->
                val g = group.Clone()
                notifyItemInserted(g.index)
                d.cancel()
            }
            d.findViewById<View>(R.id.group_dissolve).setOnClickListener { v: View? ->
                val groups: MutableList<Group> = ArrayList()
                for (g in CurrentProjectManager.CurrentProject.Groups) {
                    if (g !== group) groups.add(g)
                }
                val glp = GroupListProvider(context, "Dissolve Group Into", groups)
                d.cancel()
                glp.groupClick = GroupClick { newGroup: Group ->
                    for (kb in group.keybinds.toTypedArray()) {
                        newGroup.AddKeybind(kb, false)
                    }
                    newGroup.currentAdapter.notifyDataSetChanged()
                    CurrentProjectManager.CurrentProject.Groups.remove(group)
                    DatabaseManager.db.deleteGroup(group.id)
                    CurrentProjectManager.CurrentProject.updateGroupIndexes()
                }
                glp.Show()
            }
            d.findViewById<View>(R.id.group_move).setOnClickListener { v: View? ->
                d.cancel()
                val ap = ArrowProvider(context)
                ap.directionClicked = DirectionClicked { isUp: Boolean ->
                    val indx = CurrentProjectManager.CurrentProject.Groups.indexOf(group)
                    println(indx)
                    if (isUp) {
                        if (indx > 0) {
                            CurrentProjectManager.CurrentProject.MoveGroupUpDown(group, -1)
                            notifyItemMoved(indx, indx - 1)
                        }
                    } else if (indx < CurrentProjectManager.CurrentProject.Groups.size - 1) {
                        CurrentProjectManager.CurrentProject.MoveGroupUpDown(group, 1)
                        notifyItemMoved(indx, indx + 1)
                    }
                }
                ap.Show()
            }
            d.show()
        }
    }

    override fun getItemCount(): Int {
        return groupList?.size ?: 0
    }

    inner class GroupViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!)
}