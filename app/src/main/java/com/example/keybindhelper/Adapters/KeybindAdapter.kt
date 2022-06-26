package com.example.keybindhelper.Adapters

import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Adapters.KeybindAdapter.KeybindViewHolder
import com.example.keybindhelper.Dialogs.ArrowProvider
import com.example.keybindhelper.Dialogs.ArrowProvider.DirectionClicked
import com.example.keybindhelper.Dialogs.GroupListProvider
import com.example.keybindhelper.Dialogs.GroupListProvider.GroupClick
import com.example.keybindhelper.R
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dto.Group
import com.example.keybindhelper.dto.Keybind
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import java.util.stream.Collectors

class KeybindAdapter(private val keybindList: MutableList<Keybind>) :
    RecyclerView.Adapter<KeybindViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeybindViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.keybind_view, null)
        val params = LinearLayout.LayoutParams(parent.layoutParams)
        v.layoutParams = params
        return KeybindViewHolder(v)
    }

    override fun onBindViewHolder(holder: KeybindViewHolder, position: Int) {
        val k = keybindList[position]
        k.viewHolder = holder
        val view = holder.itemView
        updateView(k, view)
        val main = view.findViewById<LinearLayout>(R.id.keybind_main_layout)
        main.setOnClickListener {
            showEditDialog(k,
                view)
        }
        if (position == keybindList.size - 1) updateKeybindsBackground()
        main.isLongClickable = true
        main.setOnLongClickListener {
            showContextMenu(k, view)
            true
        }
    }

    private fun updateView(k: Keybind, view: View) {
        (view.findViewById<View>(R.id.keybind_name) as TextView).text = k.name
        val kb1TV = view.findViewById<TextView>(R.id.keybind_1_text)
        val kb2TV = view.findViewById<TextView>(R.id.keybind_2_text)
        val kb3TV = view.findViewById<TextView>(R.id.keybind_3_text)
        val kb1CV = view.findViewById<CardView>(R.id.keybind_1_card)
        val kb2CV = view.findViewById<CardView>(R.id.keybind_2_card)
        val kb3CV = view.findViewById<CardView>(R.id.keybind_3_card)

        //Updating text
        val kbs = arrayOf(k.kb1, k.kb2, k.kb3)
        val tvs = arrayOf(kb1TV, kb2TV, kb3TV)
        val cvs = arrayOf(kb1CV, kb2CV, kb3CV)
        for (i in kbs.indices) {
            val kb = kbs[i]
            tvs[i].text = kb
            if (kb == "") {
                cvs[i].visibility = View.GONE
            } else {
                cvs[i].visibility = View.VISIBLE
            }
        }
    }

    private fun showEditDialog(k: Keybind, view: View) {
        val c = view.context
        val d = Dialog(c)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        d.setContentView(R.layout.edit_keybind_dialog_2)
        val nameEt = d.findViewById<TextInputEditText>(R.id.edit_keybind_name)
        val kb1Et = d.findViewById<TextInputEditText>(R.id.edit_keybind_kb1)
        val kb2Et = d.findViewById<TextInputEditText>(R.id.edit_keybind_kb2)
        val kb3Et = d.findViewById<TextInputEditText>(R.id.edit_keybind_kb3)
        nameEt.setText(k.name)
        kb1Et.setText(k.kb1)
        kb2Et.setText(k.kb2)
        kb3Et.setText(k.kb3)
        d.findViewById<View>(R.id.edit_keybind_cancel_btn)
            .setOnClickListener { v: View? -> d.cancel() }
        d.findViewById<View>(R.id.edit_keybind_done_btn).setOnClickListener { v: View? ->
            val n = nameEt.text.toString()
            val error = d.findViewById<TextView>(R.id.keybind_error_text)
            if (n.length == 0) {
                error.visibility = View.VISIBLE
                error.text = "Name Cannot Be Empty"
            } else {
                k.name = n
                val kb1T = kb1Et.text.toString()
                val kb2T = kb2Et.text.toString()
                val kb3T = kb3Et.text.toString()
                var kbs = Arrays.asList(kb1T, kb2T, kb3T)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    kbs = kbs.stream().filter { z: String -> z.isNotEmpty() }
                        .collect(Collectors.toList())
                    k.kb1 = if (kbs.size > 0) kbs[0] else ""
                    k.kb2 = if (kbs.size > 1) kbs[1] else ""
                    k.kb3 = if (kbs.size > 2) kbs[2] else ""
                } else {
                    k.kb1 = kb1T
                    k.kb2 = kb2T
                    k.kb3 = kb3T
                }
                d.cancel()
                updateView(k, view)
                k.updateDB()
            }
        }
        d.show()
    }

    private fun showContextMenu(k: Keybind, view: View) {
        val context = view.context
        val d = Dialog(context)
        d.setContentView(R.layout.keybind_settings_dialog)
        (d.findViewById<View>(R.id.keybind_settings_name) as TextView).text = k.name
        d.findViewById<View>(R.id.keybind_copy).setOnClickListener { v: View? ->
            k.group!!.AddKeybind(k.Clone(false), true)
            notifyItemInserted(keybindList.size - 1)
            d.cancel()
        }
        d.findViewById<View>(R.id.keybind_delete).setOnClickListener { v: View? ->
            k.group!!.deleteKeybind(k)
            notifyItemRemoved(k.index)
            updateKeybindsBackground()
            d.cancel()
        }
        d.findViewById<View>(R.id.keybind_move).setOnClickListener { z: View? ->
            d.cancel()
            val ap = ArrowProvider(context)
            ap.directionClicked= object : DirectionClicked{
                override fun Clicked(isUp: Boolean?) {
                    val indx = k.group!!.keybinds.indexOf(k)
                    if (isUp!!) {
                        if (indx > 0) {
                            k.group!!.moveKeybindUpDown(k, -1)
                            notifyItemMoved(indx, indx - 1)
                            updateKeybindsBackground()
                        }
                    } else {
                        if (indx < k.group!!.keybinds.size - 1) {
                            k.group!!.moveKeybindUpDown(k, 1)
                            notifyItemMoved(indx, indx + 1)
                            updateKeybindsBackground()
                        }
                    }
                }
            }

            ap.Show()
        }
        d.findViewById<View>(R.id.keybind_sendtogroup).setOnClickListener { v: View? ->
            d.cancel()
            val gs: MutableList<Group?> = ArrayList()
            for (g in CurrentProjectManager.Groups!!) {
                if (g !== k.group) gs.add(g)
            }
            val glp = GroupListProvider(context, "Send Keybind To", gs)
            glp.groupClick= object : GroupClick{
                override fun GroupClicked(g: Group?) {
                    g!!.AddKeybind(k, false)
                    notifyItemRemoved(keybindList.indexOf(k))
                    try {
                        k.group!!.currentAdapter!!.notifyDataSetChanged()
                    } catch (e: Exception) {
                        System.err.println(e)
                    }
                }
            }

            glp.Show()
        }
        d.show()
    }

    private fun updateKeybindsBackground() {

        for (k in keybindList[0].group!!.keybinds) {
            val main = k.viewHolder!!.itemView.findViewById<View>(R.id.keybind_main_layout)
            if (k.index % 2 == 1) main.setBackgroundResource(R.color.offset_keybind_background) else main.setBackgroundResource(
                R.color.transparent)
        }
    }

    override fun getItemCount(): Int {
        return keybindList.size
    }

    inner class KeybindViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!)
}
