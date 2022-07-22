package com.example.keybindhelper.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.keybindhelper.MainActivity
import com.example.keybindhelper.R
import com.example.keybindhelper.Theme.ThemeManager
import com.example.keybindhelper.cloud.FirebaseDAO
import com.example.keybindhelper.cloud.IActivityResult
import com.example.keybindhelper.dao.CurrentProjectManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class SettingsFragment : Fragment() {

    private var root:View?=null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val mainActivity=activity as MainActivity;
        root=inflater.inflate(R.layout.fragment_settings,container,false)
        //root=LayoutInflater.from(this.context).inflate(R.layout.fragment_share, container)
        //todo remove this because json is only really needed for sharing projects
        root!!.findViewById<Button>(R.id.jsonButton).setOnClickListener{
            println(CurrentProjectManager.CurrentProject.getJSONObject(true).toString());
            Snackbar.make(it, "Check Run Console For JSON String", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        //google sign in button
        root!!.findViewById<SignInButton>(R.id.settings_google_btn).setOnClickListener{
            mainActivity.GoogleActivityResult= object : IActivityResult {
                override fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    googleActivityResult(requestCode,resultCode,data);
                }
            }
            FirebaseDAO.displayLogin(context!!);
        }
        displayLogin(FirebaseDAO.currentUser);
        //Sign out button
        root!!.findViewById<Button>(R.id.settings_sign_out).setOnClickListener{
            FirebaseDAO.instance.signOut();
            displayLogin(null);
        };


        if(mainActivity.Menu==null)
            mainActivity.onMenuInit=(object:MainActivity.MenuInitialized{
                override fun menuHasInitialized() {
                    initMenu(mainActivity,root!!.rootView);
                }
            })
        else
            initMenu(mainActivity,root!!.rootView);

        /**
         * Theme spinner
         */
        val spinner=root!!.findViewById<Spinner>(R.id.settings_theme_spinner);
        var items= mutableListOf<String>();
        for (theme in ThemeManager.Themes){
            items.add(theme.name);
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(ThemeManager.Themes.indexOf(ThemeManager.CurrentTheme));
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long,
            ) {
                ThemeManager.CurrentTheme=ThemeManager.Themes[position];
                ThemeManager.applyTheme();
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        //mAuth.currentUser
        return root!!
    }

    private fun initMenu(mainActivity: MainActivity, view: View) {
        mainActivity.setAppBarTitle("Settings");
        mainActivity.showMenuItems(mainActivity.settingsFragmentActionMenuIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).GoogleActivityResult=null;
        root=null;
    }
    private fun showSnackBarMessage(message: String) {
        Snackbar.make(root!!, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }
    private fun displayLogin(user:FirebaseUser?){
        val loggedInItems=root!!.findViewById<LinearLayout>(R.id.settings_logged_in_items);
        val googleSignIn=root!!.findViewById<ConstraintLayout>(R.id.settings_google_layout);
        if(user==null){
           loggedInItems.visibility=View.GONE;
           googleSignIn.visibility=View.VISIBLE;
        }else{
            loggedInItems.visibility=View.VISIBLE;
            googleSignIn.visibility=View.GONE;

            loggedInItems.findViewById<TextView>(R.id.settings_username).text=user.email;
        }
    }
    fun googleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val mAuth=FirebaseDAO.instance;
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == FirebaseDAO.REQUEST_CODE_GOOGLE_SIGN_IN) {
                try {
                    val credential = Identity.getSignInClient(context as Activity).getSignInCredentialFromIntent(data)
                    // Signed in successfully - show authenticated UI
                    println(credential.displayName)
                    println(credential.id)

                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    val firebaseCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
                    mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(context as Activity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Settings Fragment", "signInWithCredential:success")
                                FirebaseDAO.addUserToDB()//todo should this even be here?
                                showSnackBarMessage("Sign-in Successful");
                                displayLogin(FirebaseDAO.currentUser);
                            } else {
                                showSnackBarMessage("Sign-in Failed");
                                // If sign in fails, display a message to the user.
                                Log.w("Settings Fragment", "signInWithCredential:failure", task.exception)
                            }
                        }

                    //updateUI(credential)
                } catch (e: ApiException) {
                    showSnackBarMessage("API EXCEPTION (Catalog/Settings)Fragment.GoogleActivityResult")//todo figure out what fires this when u not a sleepy head :) if it even happens, maybe no internet connection?
                    // The ApiException status code indicates the detailed failure reason.
                }
            }
        }
    }

}