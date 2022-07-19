package com.example.keybindhelper.cloud

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseUser

object FirebaseDAO {
    public val REQUEST_CODE_GOOGLE_SIGN_IN = 1 /* unique request id */
    val instance: FirebaseAuth get() = FirebaseAuth.getInstance()
    val currentUser:FirebaseUser? get()= instance.currentUser
    val isUserSignedIn: Boolean  get() = currentUser == null
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