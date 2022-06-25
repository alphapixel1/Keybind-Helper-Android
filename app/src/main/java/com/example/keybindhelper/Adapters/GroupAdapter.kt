package com.example.keybindhelper.Adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Adapters.GroupAdapter.GroupViewHolder
import com.example.keybindhelper.Dialogs.ArrowProvider
import com.example.keybindhelper.Dialogs.ArrowProvider.DirectionClicked
import com.example.keybindhelper.Dialogs.GroupListProvider
import com.example.keybindhelper.Dialogs.GroupListProvider.GroupClick
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ValidatorResponse
import com.example.keybindhelper.R
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.CurrentProjectManager.MoveGroupUpDown
import com.example.keybindhelper.dao.CurrentProjectManager.isGroupNameAvailable
import com.example.keybindhelper.dao.CurrentProjectManager.updateGroupIndexes
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.dto.Group

class GroupAdapter(private val groupList: MutableList<Group>?) : RecyclerView.Adapter<GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.group_layout, parent, false)
        return GroupViewHolder(v)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList!![position]
        group.currentAdapter = this
        val context = holder.itemView.context
        val rv = holder.itemView.findViewById<RecyclerView>(R.id.keybind_zone)
        rv.adapter = KeybindAdapter(group.keybinds!!)
        rv.layoutManager = LinearLayoutManager(holder.itemView.context)
        val nameBtn = holder.itemView.findViewById<Button>(R.id.group_name_button)
        nameBtn.text = group.name
        nameBtn.setOnClickListener { v: View? ->
            val pd = PromptDialog(
                holder.itemView.context,
                "Rename Group",
                "",
                group.name,
                null
            )
            pd.validation= object : PromptDialog.Validator{
                override fun Validate(text: String?): ValidatorResponse = ValidatorResponse(
                     text == group.name || isGroupNameAvailable(text),
                     "Name Has Already Been Taken")
            }
            pd.confirmedEvent = object: PromptDialog.ConfirmedEvent{
                override fun onConfirmed(text: String?){
                    group.name = text
                    nameBtn.text = text
                    DatabaseManager.db!!.update(group)
                }
            }
            pd.ShowDialog()
        }
        holder.itemView.findViewById<View>(R.id.group_add_button).setOnClickListener { v: View? ->
            group.AddKeybind()
            rv.adapter!!.notifyItemChanged(group.keybinds!!.size - 1)
            if (rv.visibility != View.VISIBLE) rv.visibility = View.VISIBLE
        }
        holder.itemView.findViewById<View>(R.id.group_more_button).setOnClickListener { z: View? ->
            val d = Dialog(context)
            d.setContentView(R.layout.group_menu)
            (d.findViewById<View>(R.id.group_name) as TextView).text = group.name
            d.findViewById<View>(R.id.group_clear_keybinds).setOnClickListener { v: View? ->
                DatabaseManager.db!!.deleteGroupKeybinds(group.id)
                group.keybinds!!.clear()
                try {
                    rv.adapter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                }
                d.cancel()
            }
            d.findViewById<View>(R.id.group_delete).setOnClickListener { v: View? ->
                notifyItemRemoved(CurrentProjectManager.Groups!!.indexOf(group))
                CurrentProjectManager.Groups!!.remove(group)
                DatabaseManager.db!!.deleteGroup(group.id)
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
                val groups: MutableList<Group?> = ArrayList()
                for (g in CurrentProjectManager.Groups!!) {
                    if (g !== group) groups.add(g)
                }
                val glp = GroupListProvider(context, "Dissolve Group Into", groups)
                d.cancel()
                glp.groupClick= object : GroupClick {
                    override fun GroupClicked(newGroup: Group?){
                        for (kb in group.keybinds!!.toTypedArray()) {
                            newGroup!!.AddKeybind(kb, false)
                        }
                        newGroup!!.currentAdapter!!.notifyDataSetChanged()
                        CurrentProjectManager.Groups!!.remove(group)
                        updateGroupIndexes()
                    }
                }
                glp.Show()
            }
            d.findViewById<View>(R.id.group_move).setOnClickListener { v: View? ->
                d.cancel()
                val ap = ArrowProvider(context)
                ap.directionClicked= object :DirectionClicked {
                    override fun Clicked(isUp: Boolean?) {
                        val indx = CurrentProjectManager.Groups!!.indexOf(group)
                        println(indx)
                        if (isUp!!) {
                            if (indx > 0) {
                                MoveGroupUpDown(group, -1)
                                notifyItemMoved(indx, indx - 1)
                            }
                        } else if (indx < CurrentProjectManager.Groups!!.size - 1) {
                            MoveGroupUpDown(group, 1)
                            notifyItemMoved(indx, indx + 1)
                        }
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