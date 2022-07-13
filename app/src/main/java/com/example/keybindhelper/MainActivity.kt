package com.example.keybindhelper

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val allActionMenuIds= setOf(R.id.action_add,R.id.action_delete_all_groups,R.id.action_show_hide_keybinds)
    val keybindsFragmentActionMenuIds= setOf(R.id.action_add,R.id.action_delete_all_groups,R.id.action_show_hide_keybinds)
    val projectsFragmentActionMenuIds= setOf(R.id.action_add)
    val shareFragmentActionMenuIds=setOf<Int>()

    var menu:Menu?=null;
    var onMenuInit:MenuInitialized?=null;

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.appBarMain.toolbar)//todo figure out why it runs but is showing this as an error... and wont run without it? I hate kotlin so fucking much

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_keybind, R.id.nav_catalog, R.id.nav_projects), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        DatabaseManager.init(binding.root.context);
        CurrentProjectManager.loadFirstProject()
    }

    fun showMenuItems(items:Set<Int>){
        allActionMenuIds.forEach {
            this.menu?.findItem(it)!!.isVisible = items.contains(it)
        }
    }

    fun setAppBarTitle(s:String){
        supportActionBar!!.title=s;
    }

    //todo this is probably not needed, res.menu.main.xml
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        this.menu=menu;
        onMenuInit?.menuHasInitialized();

        //menu.findItem(R.id.action_settings).isVisible = false
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    interface MenuInitialized{
        fun menuHasInitialized();
    }
}