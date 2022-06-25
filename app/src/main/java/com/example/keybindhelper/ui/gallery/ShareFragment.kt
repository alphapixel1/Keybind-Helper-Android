package com.example.keybindhelper.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.keybindhelper.R;
import com.example.keybindhelper.dao.CurrentProjectManager

class ShareFragment : Fragment() {

    private var root:View?=null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root=inflater.inflate(R.layout.fragment_share,container,false)
        //root=LayoutInflater.from(this.context).inflate(R.layout.fragment_share, container)

        root!!.findViewById<Button>(R.id.jsonButton).setOnClickListener{
            println(CurrentProjectManager.CurrentProject.getJSONObject(true).toString());
        }
        return root!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root=null;
    }
}