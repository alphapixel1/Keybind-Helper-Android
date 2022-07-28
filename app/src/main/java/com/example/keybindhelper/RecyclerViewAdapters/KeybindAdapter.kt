package com.example.keybindhelper.RecyclerViewAdapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import com.example.keybindhelper.dto.Keybind
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.RecyclerViewAdapters.KeybindAdapter.KeybindViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.example.keybindhelper.R
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import android.widget.TextView
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.cardview.widget.CardView
import androidx.core.view.children
import com.google.android.material.textfield.TextInputEditText
import com.example.keybindhelper.Dialogs.ArrowProvider
import com.example.keybindhelper.Dialogs.ArrowProvider.DirectionClicked
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.Dialogs.GroupListProvider
import com.example.keybindhelper.Dialogs.GroupListProvider.GroupClick
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.Theme.ThemeManager
import com.example.keybindhelper.dto.Group
import java.lang.Exception
import java.util.*

class KeybindAdapter(private val keybindList: List<Keybind>) :
    RecyclerView.Adapter<KeybindViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeybindViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.keybind_view, null)
        val params = LinearLayout.LayoutParams(parent.layoutParams)
        v.layoutParams = params
        return KeybindViewHolder(v)
    }

    private fun applyTheme(view: View,position: Int) {
        val resources=(view.context as MainActivity).resources;
        val theme= ThemeManager.CurrentTheme!!;

        val iconTint= ColorStateList.valueOf(resources.getColor(theme.iconColor));
        view.findViewById<TextView>(R.id.keybind_name).setTextColor(iconTint)

        val cards= mutableListOf(R.id.keybind_1_card,R.id.keybind_2_card,R.id.keybind_3_card)

        val cardColor=ColorStateList.valueOf(resources.getColor(theme.keybindCardColor))
        val cardBorder=ColorStateList.valueOf(resources.getColor(theme.groupHeaderColor))

        for (card in cards){
            val c=view.findViewById<CardView>(card);
            c.backgroundTintList=cardBorder;
            (c.children.first() as CardView).backgroundTintList=cardColor
            ((c.children.first() as CardView).children.first() as TextView).setTextColor(iconTint)
        }
    }
    override fun onBindViewHolder(holder: KeybindViewHolder, position: Int) {
        val k = keybindList[position]
        applyTheme(holder.itemView,position)
        k.viewHolder = holder
        val view = holder.itemView
        val lifecycleOwner = view.context as LifecycleOwner
        k.name.observe(lifecycleOwner) { text: String? ->
            (view.findViewById<View>(R.id.keybind_name) as TextView).text = text
        }
        val kb1TV = view.findViewById<TextView>(R.id.keybind_1_text)
        k.kb1.observe(lifecycleOwner) { s: String ->
            kb1TV.text = s
            view.findViewById<View>(R.id.keybind_1_card).visibility =
                if (s.isNotEmpty()) View.VISIBLE else View.GONE
        }
        val kb2TV = view.findViewById<TextView>(R.id.keybind_2_text)
        k.kb2.observe(lifecycleOwner) { s: String ->
            kb2TV.text = s
            view.findViewById<View>(R.id.keybind_2_card).visibility =
                if (s.isNotEmpty()) View.VISIBLE else View.GONE
        }
        val kb3TV = view.findViewById<TextView>(R.id.keybind_3_text)
        k.kb3.observe(lifecycleOwner) { s: String ->
            kb3TV.text = s
            view.findViewById<View>(R.id.keybind_3_card).visibility =
                if (s.isNotEmpty()) View.VISIBLE else View.GONE
        }

        //end updating text
        val main = view.findViewById<LinearLayout>(R.id.keybind_main_layout)
        main.setOnClickListener { v: View? -> showEditDialog(k, view) }
        if (position == keybindList.size - 1)
            updateKeybindsBackground()
        main.isLongClickable = true
        main.setOnLongClickListener {
            showContextMenu(k, view)
            true
        }
    }

    /**
     * Displays a dialog for the keybind that allows you to modify it
     * @param k
     * @param view
     */
    private fun showEditDialog(k: Keybind, view: View) {
        val c = view.context
        val d = Dialog(c)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        d.setContentView(R.layout.edut_keybind_dialog_2)
        val nameEt = d.findViewById<TextInputEditText>(R.id.edit_keybind_name)
        val kb1Et = d.findViewById<TextInputEditText>(R.id.edit_keybind_kb1)
        val kb2Et = d.findViewById<TextInputEditText>(R.id.edit_keybind_kb2)
        val kb3Et = d.findViewById<TextInputEditText>(R.id.edit_keybind_kb3)
        nameEt.setText(k.name.value)
        kb1Et.setText(k.kb1.value)
        kb2Et.setText(k.kb2.value)
        kb3Et.setText(k.kb3.value)
        nameEt.requestFocus()
        val imm = d.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        d.findViewById<View>(R.id.edit_keybind_cancel_btn)
            .setOnClickListener { d.cancel() }
        d.findViewById<View>(R.id.edit_keybind_done_btn).setOnClickListener {
            val n = nameEt.text.toString()
            val error = d.findViewById<TextView>(R.id.keybind_error_text)
            if (n.isEmpty()) {
                error.visibility = View.VISIBLE
                error.text = "Name Cannot Be Empty"
            } else {
                k.name.value = n
                val kb1T = kb1Et.text.toString()
                val kb2T = kb2Et.text.toString()
                val kb3T = kb3Et.text.toString()
                val kbs = listOf(kb1T, kb2T, kb3T)
                val filteredKB: MutableList<String> = ArrayList()
                for (s in kbs) {
                    if (s.isNotEmpty())
                        filteredKB.add(s)
                }
                println(filteredKB)
                k.kb1.value = if (filteredKB.size > 0) filteredKB[0] else ""
                k.kb2.value = if (filteredKB.size > 1) filteredKB[1] else ""
                k.kb3.value = if (filteredKB.size > 2) filteredKB[2] else ""
                imm.hideSoftInputFromWindow(nameEt.windowToken, 0)
                d.dismiss()
                k.updateDB()
            }
        }
        d.setOnCancelListener{
            val imm2 = d.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm2.hideSoftInputFromWindow(nameEt.windowToken, 0)
        }
        d.findViewById<View>(R.id.keybind_more_menu_btn).setOnClickListener {
            imm.hideSoftInputFromWindow(nameEt.windowToken, 0)
            d.dismiss()
            showContextMenu(k,view);
        }
        d.show()
    }

    private fun showContextMenu(k: Keybind, view: View) {
        val context = view.context
        val d = Dialog(context)
        d.setContentView(R.layout.keybind_more_dialog)
        (d.findViewById<View>(R.id.keybind_settings_name) as TextView).text = k.name.value
        d.findViewById<View>(R.id.keybind_copy).setOnClickListener {
            k.group!!.addKeybind(k.Clone(false), true)
            notifyItemInserted(keybindList.size - 1)
            d.cancel()
        }
        d.findViewById<View>(R.id.keybind_delete).setOnClickListener {
            k.group!!.deleteKeybind(k)
            notifyItemRemoved(k.index)
            updateKeybindsBackground()
            d.cancel()
        }
        d.findViewById<View>(R.id.keybind_move).setOnClickListener {
            d.cancel()
            val ap = ArrowProvider(context)
            ap.directionClicked = object : DirectionClicked {
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
        d.findViewById<View>(R.id.keybind_sendtogroup).setOnClickListener {
            d.cancel()
            val gs: MutableList<Group> = ArrayList()
            for (g in CurrentProjectManager.CurrentProject!!.Groups!!) {
                if (g !== k.group) gs.add(g)
            }
            val glp = GroupListProvider(context, "Send Keybind To", gs)
            glp.groupClick = object : GroupClick {
                override fun GroupClicked(g: Group?) {
                    g!!.addKeybind(k, false)
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

    /**
     * Gives keybinds an alternating background color
     */
    private fun updateKeybindsBackground() {
        if(keybindList.isNotEmpty()) {
            keybindList[0].group!!.keybinds.forEach {
                val main = it.viewHolder!!.itemView.findViewById<View>(R.id.keybind_main_layout)
                if ((it.index) % 2 == 1)
                    main.setBackgroundResource(ThemeManager.CurrentTheme!!.offsetKeybindBackgroundColor)
                else
                    main.setBackgroundResource(ThemeManager.CurrentTheme!!.keybindBackgroundColor)
            }
        }
    }

    override fun getItemCount(): Int {
        return keybindList.size
    }

    inner class KeybindViewHolder(itemView: View?) : RecyclerView.ViewHolder( itemView!!)
}