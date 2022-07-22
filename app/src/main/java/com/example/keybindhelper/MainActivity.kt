package com.example.keybindhelper

import android.content.Intent
import android.content.res.ColorStateList
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
import com.example.keybindhelper.Theme.ThemeManager
import com.example.keybindhelper.cloud.IActivityResult
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val allActionMenuIds= setOf(R.id.action_add,R.id.action_delete_all_groups,R.id.action_show_hide_keybinds,R.id.action_cloud)
    val keybindsFragmentActionMenuIds= setOf(R.id.action_add,R.id.action_delete_all_groups,R.id.action_show_hide_keybinds)
    val projectsFragmentActionMenuIds= setOf(R.id.action_add,R.id.action_cloud)
    val settingsFragmentActionMenuIds=setOf<Int>()


    /**
     Google Event Handler for sign in
     **/
    var GoogleActivityResult:IActivityResult?=null;
    /**
     * Menu Initialization
     */
    var Menu:Menu?=null;
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
            R.id.nav_keybind, R.id.nav_settings, R.id.nav_projects), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        DatabaseManager.init(binding.root.context);
        ThemeManager.init(this);
        ThemeManager.applyTheme();
        CurrentProjectManager.loadFirstProject()


    }

    fun showMenuItems(items:Set<Int>){
        allActionMenuIds.forEach{
            this.Menu?.findItem(it)!!.isVisible = items.contains(it)
        }
    }
    fun setAppBarTitle(s:String){
        supportActionBar!!.title=s;
    }


    //todo this is probably not needed, res.menu.main.xml
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        this.Menu=menu;
        onMenuInit?.menuHasInitialized();
        makeMenuThemeColor(menu)
        return true
    }

    private fun makeMenuThemeColor(menu:Menu){
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            spanString.setSpan(ForegroundColorSpan(ThemeManager.CurrentTheme!!.iconColor),0,spanString.length,0)//fix the color to white
            item.title = spanString
            if(item.hasSubMenu())
                makeMenuThemeColor(item.subMenu)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    interface MenuInitialized{
        fun menuHasInitialized();
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        GoogleActivityResult?.onResult(requestCode,resultCode,data);
    }
    fun applyTheme(){
        //throw Exception("waaa");
        val currentTheme=ThemeManager.CurrentTheme!!;
        if(Menu!=null)
            makeMenuThemeColor(Menu!!)
        println("changing theme?");
        val toolbar=binding.root.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        //toolbar.setBackgroundColor(R.color.white)

        toolbar.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(currentTheme.appColor)));

       // toolbar.background=getDrawable(R.drawable.disabled_cloud_24);
        //toolbar.visibility= View.GONE;
        //this.appBarConfiguration.
    }
}