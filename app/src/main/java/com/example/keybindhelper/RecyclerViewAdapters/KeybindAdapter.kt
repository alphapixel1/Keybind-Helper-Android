package com.example.keybindhelper.RecyclerViewAdapters

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
import android.view.View.OnLongClickListener
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
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

        //view.findViewById<RecyclerView>(R.id.keybind_zone).backgroundTintList= ColorStateList.valueOf(resources.getColor(theme.keybindBackgroundColor))

        val iconTint= ColorStateList.valueOf(resources.getColor(theme.iconColor));
        view.findViewById<TextView>(R.id.keybind_name).setTextColor(iconTint)

        val cards= mutableListOf<Int>(R.id.keybind_1_card,R.id.keybind_2_card,R.id.keybind_3_card)
        val cardColor=ColorStateList.valueOf(resources.getColor(theme.keybindCardColor))
        val cardBorder=ColorStateList.valueOf(resources.getColor(theme.groupHeaderColor))
        for (card in cards){
            val c=view.findViewById<CardView>(card);
            c.backgroundTintList=cardBorder;
            c.children.first().backgroundTintList=cardColor;
            ((c.children.first() as CardView).children.first() as TextView).setTextColor(iconTint)
        }
        if(position%2==0){

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
                if (s.length > 0) View.VISIBLE else View.GONE
        }
        val kb2TV = view.findViewById<TextView>(R.id.keybind_2_text)
        k.kb2.observe(lifecycleOwner) { s: String ->
            kb2TV.text = s
            view.findViewById<View>(R.id.keybind_2_card).visibility =
                if (s.length > 0) View.VISIBLE else View.GONE
        }
        val kb3TV = view.findViewById<TextView>(R.id.keybind_3_text)
        k.kb3.observe(lifecycleOwner) { s: String ->
            kb3TV.text = s
            view.findViewById<View>(R.id.keybind_3_card).visibility =
                if (s.length > 0) View.VISIBLE else View.GONE
        }

        //end updating text
        val main = view.findViewById<LinearLayout>(R.id.keybind_main_layout)
        main.setOnClickListener { v: View? -> showEditDialog(k, view) }
        if (position == keybindList.size - 1)
            updateKeybindsBackground()
        main.isLongClickable = true
        main.setOnLongClickListener { v: View? ->
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
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        d.findViewById<View>(R.id.edit_keybind_cancel_btn)
            .setOnClickListener { v: View? -> d.cancel() }
        d.findViewById<View>(R.id.edit_keybind_done_btn).setOnClickListener { v: View? ->
            val n = nameEt.text.toString()
            val error = d.findViewById<TextView>(R.id.keybind_error_text)
            if (n.length == 0) {
                error.visibility = View.VISIBLE
                error.text = "Name Cannot Be Empty"
            } else {
                k.name.value = n
                val kb1T = kb1Et.text.toString()
                val kb2T = kb2Et.text.toString()
                val kb3T = kb3Et.text.toString()
                val kbs = Arrays.asList(kb1T, kb2T, kb3T)
                val filteredKB: MutableList<String> = ArrayList()
                for (s in kbs) {
                    if (s.length > 0) filteredKB.add(s)
                }
                println(filteredKB)
                //throw new NotImplementedError();
                k.kb1.value = if (filteredKB.size > 0) filteredKB[0] else ""
                k.kb2.value = if (filteredKB.size > 1) filteredKB[1] else ""
                k.kb3.value = if (filteredKB.size > 2) filteredKB[2] else ""
                imm.hideSoftInputFromWindow(nameEt.windowToken, 0)
                d.cancel()
                //updateView(k, view);
                k.updateDB()
            }
        }
        d.show()
    }

    private fun showContextMenu(k: Keybind, view: View) {
        val context = view.context
        val d = Dialog(context)
        d.setContentView(R.layout.keybind_more_dialog)
        (d.findViewById<View>(R.id.keybind_settings_name) as TextView).text = k.name.value
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
            ap.directionClicked = object : DirectionClicked {
                override fun Clicked(isUp: Boolean?) {
                    val indx = k.group!!.keybinds!!.indexOf(k)
                    if (isUp!!) {
                        if (indx > 0) {
                            k.group!!.moveKeybindUpDown(k, -1)
                            notifyItemMoved(indx, indx - 1)
                            updateKeybindsBackground()
                        }
                    } else {
                        if (indx < k.group!!.keybinds!!.size - 1) {
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
            val gs: MutableList<Group> = ArrayList()
            for (g in CurrentProjectManager.CurrentProject!!.Groups!!) {
                if (g !== k.group) gs.add(g)
            }
            val glp = GroupListProvider(context, "Send Keybind To", gs)
            glp.groupClick = object : GroupClick {
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

    /**
     * Gives keybinds an alternating background color
     */
    private fun updateKeybindsBackground() {

        for (k in keybindList[0].group!!.keybinds!!) {
            val main = k.viewHolder!!.itemView.findViewById<View>(R.id.keybind_main_layout)
            if (k.index % 2 == 1) main.setBackgroundResource(ThemeManager.CurrentTheme!!.offsetKeybindBackgroundColor) else main.setBackgroundResource(//R.color.offset_keybind_background
                ThemeManager.CurrentTheme!!.keybindBackgroundColor)
        }
    }

    override fun getItemCount(): Int {
        return keybindList.size
    }

    inner class KeybindViewHolder(itemView: View?) : RecyclerView.ViewHolder( itemView!!)
}