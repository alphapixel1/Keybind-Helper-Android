package com.example.keybindhelper.cloud

data class FirebaseProject(val name:String,val documentID:String){
    fun toHashMap():HashMap<String,String>{
        return hashMapOf(
            "name" to name,
            "projectID" to documentID
        )
    }
}