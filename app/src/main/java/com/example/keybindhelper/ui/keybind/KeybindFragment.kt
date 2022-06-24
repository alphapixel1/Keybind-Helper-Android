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
import com.example.keybindhelper.Dialogs.PromptDialog
import com.example.keybindhelper.Dialogs.ConfirmDialog
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.Room.Adapters.GroupAdapter
import com.example.keybindhelper.Room.CurrentProject
import com.example.keybindhelper.databinding.KeybindViewBinding


class KeybindFragment : Fragment() {

    private var _binding: KeybindViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
       /* val homeViewModel =
            ViewModelProvider(this).get(KeybindViewModel::class.java)
        */
        //_binding = KeybindViewBinding.inflate(inflater, container, false)

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

        if(CurrentProject.Groups==null) {
            CurrentProject.isProjectLoaded.observe(viewLifecycleOwner) {
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
        mainActivity.setAppBarTitle(CurrentProject.CurrentProject.name);
        mainActivity.showMenuItems(mainActivity.keybindsFragmentActionMenuIds)
        val rv=view.findViewById<RecyclerView>(R.id.recyclerView);
        mainActivity.Menu!!.findItem(R.id.action_delete_all_groups).setOnMenuItemClickListener {
            val cd=ConfirmDialog(view.context,"Delete All Groups?")
            cd.onConfirmed= ConfirmDialog.ConfirmedEvent {
                CurrentProject.deleteAllGroups()
                rv.adapter = GroupAdapter(CurrentProject.Groups)
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
            CurrentProject.AddGroup()
            System.out.println("MainActivity.floatingactionbutton.click: Groups Size:" + CurrentProject.Groups.size)
            rv.adapter!!.notifyItemChanged(CurrentProject.Groups.size - 1)
            true;
        }
    }

    private fun loadRecycleView(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView);
        rv!!.adapter = GroupAdapter(CurrentProject.Groups);
        rv.layoutManager = LinearLayoutManager(root.context);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
