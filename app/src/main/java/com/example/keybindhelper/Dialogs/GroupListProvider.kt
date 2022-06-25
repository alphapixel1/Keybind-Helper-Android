package com.example.keybindhelper.Dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.keybindhelper.dto.Group

class GroupListProvider(c: Context?, title: String?, groups: MutableList<Group?>) {
    @JvmField
    var groupClick: GroupClick? = null
    private val d: Dialog
    fun Show() {
        d.show()
    }

    interface GroupClick {
        fun GroupClicked(g: Group?)
    }

    init {
        d = Dialog(c!!)
        val line = LinearLayout(c)
        line.orientation = LinearLayout.VERTICAL
        line.setPadding(40, 40, 40, 40)
        d.setContentView(line)
        val titleView = TextView(c)
        titleView.text = title
        titleView.textSize = 30f
        line.addView(titleView)
        val s = ScrollView(c)
        line.addView(s)
        val scrollLine = LinearLayout(c)
        scrollLine.orientation = LinearLayout.VERTICAL
        s.addView(scrollLine)
        for (g in groups) {
            val b = Button(c)
            b.text = g!!.name
            b.setOnClickListener { v: View? ->
                groupClick!!.GroupClicked(g)
                d.cancel()
            }
            scrollLine.addView(b)
        }
        if (groups.size == 0) {
            val noitems = TextView(c)
            noitems.text = "None"
            noitems.textAlignment = View.TEXT_ALIGNMENT_CENTER
            line.addView(noitems)
        }
    }
}