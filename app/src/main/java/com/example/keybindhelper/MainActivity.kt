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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import com.example.keybindhelper.dto.Project
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

        addArma3()
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
            spanString.setSpan(ForegroundColorSpan(Color.WHITE),0,spanString.length,0)//fix the color to white
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
        val currentTheme=ThemeManager.CurrentTheme!!;
        println("changing theme?");
        val toolbar=findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        //todo   changes toolbar color, lets not    toolbar.backgroundTintList = ColorStateList.valueOf(resources.getColor(currentTheme.appColor));

        val backgroundColor=ColorStateList.valueOf(getColor(currentTheme.backgroundColor))//resources.getColor(currentTheme.backgroundColor));
        findViewById<ConstraintLayout>(R.id.main_background).backgroundTintList = backgroundColor

    }
    //adds ready or not to the projects list
    fun addReadyOrNot(){
         Project.fromJSONString("{\n" +
                "    \"projectName\": \"Ready Or Not\",\n" +
                "    \"groups\": [\n" +
                "        {\n" +
                "            \"groupName\": \"Misc\",\n" +
                "            \"keybinds\": [\n" +
                "                {\n" +
                "                    \"keybindName\": \"Tablet\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"End\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Chemlight\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"V\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Secondary Sight\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"P\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Melee\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"B\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"groupName\": \"Teamwork\",\n" +
                "            \"keybinds\": [\n" +
                "                {\n" +
                "                    \"keybindName\": \"AI Command Interface\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"MMB\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Team View Cam\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"T\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Push To Talk\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"Caps Lock\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Cycle Voice Channels\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"L\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Chat\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"J\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Team Chat\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"K\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"groupName\": \"Movement\",\n" +
                "            \"keybinds\": [\n" +
                "                {\n" +
                "                    \"keybindName\": \"Hold Crouch\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"Ctrl\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Crouch\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"C\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"groupName\": \"Gun\",\n" +
                "            \"keybinds\": [\n" +
                "                {\n" +
                "                    \"keybindName\": \"Fire Select\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"X\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Laser/Flashlight\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"Pause\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"keybindName\": \"Canted Sight\",\n" +
                "                    \"keybinds\": [\n" +
                "                        \"O\",\n" +
                "                        \"\",\n" +
                "                        \"\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}")
    }

    fun addArma3(){
        Project.fromJSONString("{\"projectName\":\"Arma 3\",\"groups\":[{\"groupName\":\"Movement\",\"keybinds\":[{\"keybindName\":\"Combat Pace Toggle\",\"keybinds\":[\"2xC\"]},{\"keybindName\":\"Walk/Run Toggle\",\"keybinds\":[\"Left Ctrl+C\"]},{\"keybindName\":\"Stance Change\",\"keybinds\":[\"L Ctrl W/A/S/D\"]},{\"keybindName\":\"Crouch\",\"keybinds\":[\"X\"]},{\"keybindName\":\"Prone\",\"keybinds\":[\"Z\"]},{\"keybindName\":\"Swim Up\",\"keybinds\":[\"X\"]},{\"keybindName\":\"Swim Down\",\"keybinds\":[\"Z\"]},{\"keybindName\":\"Step Over\",\"keybinds\":[\"V\"]},{\"keybindName\":\"Salute\",\"keybinds\":[\"\\\\\"]},{\"keybindName\":\"Sit Down\",\"keybinds\":[\"'\"]}]},{\"groupName\":\"View\",\"keybinds\":[{\"keybindName\":\"Toggle View\",\"keybinds\":[\"Tab\"]},{\"keybindName\":\"Zoom In\",\"keybinds\":[\"1\"]},{\"keybindName\":\"Zoom Out\",\"keybinds\":[\"2\"]}]},{\"groupName\":\"Multiplayer\",\"keybinds\":[{\"keybindName\":\"Statistics\",\"keybinds\":[\"P\"]},{\"keybindName\":\"List Of Players\",\"keybinds\":[\"RCtrl + P\"]},{\"keybindName\":\"Next Channel\",\"keybinds\":[\".\"]},{\"keybindName\":\"Previous Channel\",\"keybinds\":[\",\"]},{\"keybindName\":\"Push To Talk\",\"keybinds\":[\"Left Win\"]},{\"keybindName\":\"Tactical Ping\",\"keybinds\":[\"L Shift + T\"]}]},{\"groupName\":\"Common\",\"keybinds\":[{\"keybindName\":\"Task Overview\",\"keybinds\":[\"J\"]},{\"keybindName\":\"GPS Toggle\",\"keybinds\":[\"Ctrl + A\"]},{\"keybindName\":\"Targeting Camera\",\"keybinds\":[\"Ctrl + LMB\"]},{\"keybindName\":\"Compas\",\"keybinds\":[\"K\"]},{\"keybindName\":\"Watch\",\"keybinds\":[\"O\"]}]},{\"groupName\":\"Weapons\",\"keybinds\":[{\"keybindName\":\"Next Fire Mode\",\"keybinds\":[\"F\"]},{\"keybindName\":\"Hold Breath\",\"keybinds\":[\"L Shift\"]},{\"keybindName\":\"Optics\",\"keybinds\":[\"R Alt\"]},{\"keybindName\":\"Cycle Throwable\",\"keybinds\":[\"L Ctrl + G\"]},{\"keybindName\":\"Gun Elevation\",\"keybinds\":[\"PgUp\",\"PgDn\"]},{\"keybindName\":\"Lase Range\",\"keybinds\":[\"T\"]},{\"keybindName\":\"Laser\",\"keybinds\":[\"L\"]}]},{\"groupName\":\"Vehicle\",\"keybinds\":[{\"keybindName\":\"Hand Break\",\"keybinds\":[\"X\"]},{\"keybindName\":\"Turn In\",\"keybinds\":[\"L Ctrl + Q\"]},{\"keybindName\":\"Turn Out\",\"keybinds\":[\"L Ctrl + E\"]}]},{\"groupName\":\"Helicopter\",\"keybinds\":[{\"keybindName\":\"Collective Raise\",\"keybinds\":[\"L Shift\"]},{\"keybindName\":\"Collective Lower\",\"keybinds\":[\"Z\"]},{\"keybindName\":\"Sling Load Assistant\",\"keybinds\":[\"R Ctrl + B\"]},{\"keybindName\":\"Rope Interaction\",\"keybinds\":[\"B\"]}]},{\"groupName\":\"Plane\",\"keybinds\":[{\"keybindName\":\"Increase Thrust\",\"keybinds\":[\"L Shift\"]},{\"keybindName\":\"Decrease Thrust\",\"keybinds\":[\"Z\"]},{\"keybindName\":\"Auto Vectoring\",\"keybinds\":[\"X\"]},{\"keybindName\":\"Flaps\",\"keybinds\":[\"L Ctrl + Scroll\"]}]}]}");
    }
}