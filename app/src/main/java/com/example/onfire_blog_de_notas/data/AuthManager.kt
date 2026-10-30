package com.example.onfire_blog_de_notas.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object AuthManager {
    fun getSafeAuth(context: Context? = null): FirebaseAuth? {
        return try {
            if (context != null) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (_: Throwable) {
            null
        }
    }

    val currentUser: FirebaseUser?
        get() = try {
            FirebaseAuth.getInstance().currentUser
        } catch (_: Throwable) {
            null
        }

    fun signInWithGoogleCredential(
        context: Context,
        credential: AuthCredential,
        onResult: (success: Boolean, userEmail: String?, userName: String?, errorMessage: String?) -> Unit
    ) {
        val firebaseAuth = getSafeAuth(context)
        if (firebaseAuth == null) {
            onResult(false, null, null, "Firebase no está inicializado")
            return
        }

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val email = user?.email ?: ""
                    val name = user?.displayName ?: email.substringBefore("@")
                    onResult(true, email, name, null)
                } else {
                    onResult(false, null, null, task.exception?.localizedMessage ?: "Error al iniciar con Google")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, null, null, e.localizedMessage)
            }
    }

    fun signOut(context: Context) {
        try {
            FirebaseAuth.getInstance().signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, gso).signOut()
        } catch (_: Exception) {}
    }
}
