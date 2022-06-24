package com.example.keybindhelper.ui.keybind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keybindhelper.Dialogs.ConfirmDialog
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.Adapters.GroupAdapter
import com.example.keybindhelper.dao.CurrentProjectManager


class KeybindFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val root: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_keybind,container,false)//binding.root

        val mainActivity=activity as MainActivity;
        if(mainActivity.Menu==null)
            mainActivity.onMenuInit=(object:MainActivity.MenuInitialized{
                override fun menuHasInitialized() {
                    initMenu(mainActivity,root);
                }
            })
        else
            initMenu(mainActivity,root);

        if(CurrentProjectManager.Groups==null) {
            CurrentProjectManager.isProjectLoaded.observe(viewLifecycleOwner) {
                println("LOADED")
                loadRecycleView(root);
            }
        }else{
            println("already loaded")
            loadRecycleView(root);
        }


        return root
    }

    private fun initMenu(mainActivity: MainActivity,view: View) {
        mainActivity.setAppBarTitle(CurrentProjectManager.CurrentProject.name);
        mainActivity.showMenuItems(mainActivity.keybindsFragmentActionMenuIds)
        val rv=view.findViewById<RecyclerView>(R.id.recyclerView);

        mainActivity.Menu!!.findItem(R.id.action_delete_all_groups).setOnMenuItemClickListener {
            val cd=ConfirmDialog(view.context,"Delete All Groups?")
            cd.onConfirmed= ConfirmDialog.ConfirmedEvent {
                CurrentProjectManager.deleteAllGroups()
                rv.adapter = GroupAdapter(CurrentProjectManager.Groups)
            }
            cd.Show()

            true;
        }
        mainActivity.Menu!!.findItem(R.id.action_sub_hide_keybinds).setOnMenuItemClickListener {
            for(v in rv.children)
                v.findViewById<RecyclerView>(R.id.keybind_zone).isVisible = false;
            true;
        }
        mainActivity.Menu!!.findItem(R.id.action_sub_show_keybinds).setOnMenuItemClickListener {
            for(v in rv.children)
                v.findViewById<RecyclerView>(R.id.keybind_zone).isVisible=true;
            true;
        }
        mainActivity.Menu!!.findItem(R.id.action_add).setOnMenuItemClickListener {
            CurrentProjectManager.AddGroup()
            System.out.println("MainActivity.floatingactionbutton.click: Groups Size:" + CurrentProjectManager.Groups.size)
            rv.adapter!!.notifyItemChanged(CurrentProjectManager.Groups.size - 1)
            true;
        }
    }

    private fun loadRecycleView(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView);
        rv!!.adapter = GroupAdapter(CurrentProjectManager.Groups);
        rv.layoutManager = LinearLayoutManager(root.context);
    }


}
