package com.example.keybindhelper.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.dao.CurrentProjectManager
import com.google.android.material.snackbar.Snackbar

class CatalogFragment : Fragment() {

    private var root:View?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root=inflater.inflate(R.layout.fragment_settings,container,false)
        //root=LayoutInflater.from(this.context).inflate(R.layout.fragment_share, container)

        root!!.findViewById<Button>(R.id.jsonButton).setOnClickListener{
            println(CurrentProjectManager.CurrentProject.getJSONObject(true).toString())
            Snackbar.make(it, "Check Run Console For JSON String", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val mainActivity=activity as MainActivity
        if(mainActivity.menu==null)
            mainActivity.onMenuInit=(object:MainActivity.MenuInitialized{
                override fun menuHasInitialized() {
                    initMenu(mainActivity)
                }
            })
        else
            initMenu(mainActivity)


        //mAuth.currentUser
        return root!!
    }

    private fun initMenu(mainActivity: MainActivity) {
        mainActivity.setAppBarTitle("Share "+CurrentProjectManager.CurrentProject.name.value)
        mainActivity.showMenuItems(mainActivity.shareFragmentActionMenuIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root=null
    }
    //fuck this firebase bullshit I hate this so much

}