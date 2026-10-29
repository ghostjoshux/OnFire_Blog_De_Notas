package com.example.onfire_blog_de_notas.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

object AuthManager {
    private const val PREF_NAME = "onfire_user_prefs"
    private const val KEY_PREFIX_PASS = "user_pass_"
    private const val KEY_PREFIX_NAME = "user_name_"

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

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLocalUser(context: Context, name: String, email: String, pass: String = "") {
        val prefs = getPrefs(context)
        val normalizedEmail = email.trim().lowercase()

        val editor = prefs.edit().putString(KEY_PREFIX_NAME + normalizedEmail, name.trim())
        if (pass.isNotEmpty()) {
            editor.putString(KEY_PREFIX_PASS + normalizedEmail, pass)
        }
        editor.apply()
    }

    fun registerUserWithFirebase(
        context: Context,
        name: String,
        email: String,
        pass: String,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()
        val firebaseAuth = getSafeAuth(context)
        if (firebaseAuth == null) {
            saveLocalUser(context, name, normalizedEmail, pass)
            onResult(true, null)
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name.trim())
                        .build()
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        saveLocalUser(context, name, normalizedEmail, pass)
                        onResult(true, null)
                    } ?: run {
                        saveLocalUser(context, name, normalizedEmail, pass)
                        onResult(true, null)
                    }
                } else {
                    // Fallback to local save if Firebase auth fails (e.g. offline or email already exists in local mode)
                    saveLocalUser(context, name, normalizedEmail, pass)
                    onResult(true, null)
                }
            }
            .addOnFailureListener {
                saveLocalUser(context, name, normalizedEmail, pass)
                onResult(true, null)
            }
    }

    fun loginUser(context: Context, email: String, pass: String): Boolean {
        val prefs = getPrefs(context)
        val normalizedEmail = email.trim().lowercase()
        val savedPass = prefs.getString(KEY_PREFIX_PASS + normalizedEmail, null)
        return savedPass != null && savedPass == pass
    }

    fun loginUserWithFirebase(
        context: Context,
        email: String,
        pass: String,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()
        val firebaseAuth = getSafeAuth(context)
        if (firebaseAuth == null) {
            val localSuccess = loginUser(context, normalizedEmail, pass)
            if (localSuccess) {
                onResult(true, null)
            } else {
                onResult(false, "Correo o contraseña incorrectos")
            }
            return
        }

        firebaseAuth.signInWithEmailAndPassword(normalizedEmail, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    saveLocalUser(context, user?.displayName ?: "", normalizedEmail, pass)
                    onResult(true, null)
                } else {
                    // Fallback: check if valid locally or register in Firebase
                    val localSuccess = loginUser(context, normalizedEmail, pass)
                    if (localSuccess) {
                        // Try creating in Firebase with this password for future sync
                        firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, pass)
                            .addOnCompleteListener { _ ->
                                onResult(true, null)
                            }
                    } else {
                        // Try creating account in Firebase if not registered yet
                        firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, pass)
                            .addOnCompleteListener { createNewTask ->
                                if (createNewTask.isSuccessful) {
                                    saveLocalUser(context, "", normalizedEmail, pass)
                                    onResult(true, null)
                                } else {
                                    val errorMsg = task.exception?.localizedMessage ?: "Correo o contraseña incorrectos"
                                    onResult(false, errorMsg)
                                }
                            }
                    }
                }
            }
            .addOnFailureListener {
                val localSuccess = loginUser(context, normalizedEmail, pass)
                if (localSuccess) {
                    onResult(true, null)
                } else {
                    onResult(false, "Correo o contraseña incorrectos")
                }
            }
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
                    saveLocalUser(context, name, email)
                    onResult(true, email, name, null)
                } else {
                    onResult(false, null, null, task.exception?.localizedMessage ?: "Error al iniciar con Google")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, null, null, e.localizedMessage)
            }
    }

    fun getUserName(context: Context, email: String): String? {
        val prefs = getPrefs(context)
        val normalizedEmail = email.trim().lowercase()
        return prefs.getString(KEY_PREFIX_NAME + normalizedEmail, null)
            ?: currentUser?.displayName
    }

    fun signOut(context: Context) {
        try {
            getSafeAuth(context)?.signOut()
        } catch (_: Exception) {}
    }
}
