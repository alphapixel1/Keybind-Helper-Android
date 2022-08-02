package com.example.keybindhelper.cloud

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.example.keybindhelper.ITaskResponse
import com.example.keybindhelper.dao.CurrentProjectManager
import com.example.keybindhelper.dto.Project
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object FirebaseDAO {
    /**
     * Login Vals
     */
    val REQUEST_CODE_GOOGLE_SIGN_IN = 1 /* unique request id */
    val instance: FirebaseAuth get() = FirebaseAuth.getInstance()
    val currentUser:FirebaseUser? get()= instance.currentUser
    val isUserSignedIn: Boolean  get() = currentUser != null

    /**
     * Firestore? todo aaahhhhh
     */
    val maxProjects=5;
    val database:FirebaseFirestore =FirebaseFirestore.getInstance()
    fun addUserToDB(){
        if(currentUser!=null){
            val usersRef=database.collection("users").document(currentUser!!.uid);
            usersRef.get().addOnCompleteListener {
                if (it.result.exists()){
                    println("FirebaseDAO: user already exists, no document created");
                }else{
                    val map =hashMapOf(
                    "Projects" to mutableListOf<Any>(),
                    )
                    usersRef.set(map)

                    //database.collection("users").add(map);
                }
            }
        }
    }
    fun getUserProjects(result: ITaskResponse<MutableList<FirebaseProject>>){
        if(currentUser!=null) {
            database.collection("users").document(currentUser!!.uid).get().addOnSuccessListener {
                if(it.data==null) {
                    result.onResponse(mutableListOf())
                    addUserToDB()
                }else {
                    val projects = it.data!!["Projects"] as ArrayList<HashMap<String, String>>;
                    var projectsReturn = mutableListOf<FirebaseProject>();
                    for (project in projects) {
                        //println(project::class.java.typeName)

                        projectsReturn.add(
                            FirebaseProject(project["name"]!!, project["projectID"]!!)
                        )
                    }
                    result.onResponse(projectsReturn);
                }
            }
        }else{
            System.err.println("FirebaseDAO.getUserProjects: User should not be null here");
        }
    }
    fun getUninqueProjectName(name:String,projects:List<FirebaseProject>):String{
        if(isProjectNameUnique(name,projects))
            return name;
        var i=1;
        while (!isProjectNameUnique("$name ($i)",projects)){
            i++;
        }
        return "$name ($i)"
    }
    private fun isProjectNameUnique(name:String,projects:List<FirebaseProject>):Boolean{
        for (p in projects)
            if(p.name == name)
                return false;
        return true;
    }

    /**
     * Adds the project to cloud firestore by Adding it to the users projects array and projects collection
     */
    fun addProject(project:Project,name:String,response: ITaskResponse<Boolean>){
        var data="";
        if(project==CurrentProjectManager.CurrentProject){
            data=project.getJSONObject(true).toString();
        }else{
            data=project.getJSONObject(false).toString();
        }
        getUserProjects(object: ITaskResponse<MutableList<FirebaseProject>> {
            override fun onResponse(projects: MutableList<FirebaseProject>) {
                val projectsRef=database.collection("projects");
                projectsRef.add(hashMapOf(
                    "Data" to data
                )).addOnSuccessListener {
                    projects.add(FirebaseProject(name,it.id));
                    database.collection("users")
                        .document(currentUser!!.uid).set(
                            hashMapOf<String,Any>(
                                "Projects" to projects.map {p->
                                    p.toHashMap()
                                }
                            )
                    ).addOnCompleteListener {
                        response.onResponse(it.isSuccessful)
                    }

                }.addOnFailureListener {
                    response.onResponse(false);
                }
            }

        })
    }

    fun download(projectName: String,projects: List<FirebaseProject>,result:ITaskResponse<String>) {
        val pToDownload=projects.firstOrNull{it.name==projectName}
        if(pToDownload==null){
            result.onResponse("An Error Occurred");
        }else{
            database.collection("projects").document(pToDownload.documentID).get().addOnCompleteListener{
                if(it.isSuccessful){
                    try {
                        val p = Project.fromJSONString(it.result.data!!["Data"].toString());
                        result.onResponse("""Successfully Downloaded as "${p.name.value}"""")
                    }catch (e:Exception){

                        result.onResponse("An Error Occurred while downloading: Unable to download.")
                    }
                }else{
                    result.onResponse("An Error Occurred: Could not find project document")
                }
            }
        }

    }
    //todo this shit
    fun delete(projectName: String,projects: List<FirebaseProject>,result:ITaskResponse<String>){
        var pToDelete=projects.firstOrNull{it.name==projectName}
        println("WHAT IS THE WORD: "+ pToDelete.toString())
        if(pToDelete!=null) {
            database.collection("projects").document(pToDelete.documentID).delete().addOnCompleteListener{
                if(it.isSuccessful){
                    val userRef=database.collection("users").document(currentUser!!.uid);
                    val map =hashMapOf(
                        "Projects" to   projects.filter {
                            it.name!=projectName
                        }.map {p->
                            p.toHashMap()
                        }
                    )
                    userRef.set(map).addOnCompleteListener {
                        if(it.isSuccessful)
                            result.onResponse("Successfully Deleted \"${pToDelete.name}\"");
                            else
                            result.onResponse("FATAL ERROR: An Error Occurred While Deleting project doc deleted but not user");
                    }
                }else{
                    result.onResponse("An Error Occurred While Deleting");
                }

            }




        }else{
            result.onResponse("Failed to delete.");
        }
    }



        fun displayLogin(context: Context) {
            val request = GetSignInIntentRequest.builder()
                .setServerClientId("736787936921-7fg093rr6r35ptugkk7gn5epnvssgrl0.apps.googleusercontent.com")//todo replace with get string
                .build()

            Identity.getSignInClient(context)
                .getSignInIntent(request)
                .addOnSuccessListener { result ->
                    try {

                        startIntentSenderForResult(
                            context as Activity,
                            result.intentSender,  /* fillInIntent= */
                            REQUEST_CODE_GOOGLE_SIGN_IN,  /* flagsMask= */
                            null,  /* flagsValue= */
                            0,  /* extraFlags= */
                            0,  /* options= */
                            0,
                            null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("FirebaseDAO.kt", "Google Sign-in failed")
                    }
                }
                .addOnFailureListener { e -> Log.e("FirebaseDAO.kt", "Google Sign-in failed", e) }
        }


}
