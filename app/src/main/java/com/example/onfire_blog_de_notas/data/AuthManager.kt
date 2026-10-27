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
        if (context == null) return null
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                null
            } else {
                FirebaseAuth.getInstance()
            }
        } catch (_: Throwable) {
            null
        }
    }

    val currentUser: FirebaseUser?
        get() = null

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
            onResult(true, "Registrado localmente")
            return
        }

        try {
            firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name.trim())
                            .build()
                        user?.updateProfile(profileUpdates)

                        saveLocalUser(context, name, normalizedEmail, pass)
                        onResult(true, null)
                    } else {
                        saveLocalUser(context, name, normalizedEmail, pass)
                        val errorMsg = task.exception?.localizedMessage ?: "Error Firebase"
                        onResult(true, "Registrado localmente ($errorMsg)")
                    }
                }
                .addOnFailureListener { e ->
                    saveLocalUser(context, name, normalizedEmail, pass)
                    onResult(true, "Registrado localmente (${e.localizedMessage})")
                }
        } catch (e: Exception) {
            saveLocalUser(context, name, normalizedEmail, pass)
            onResult(true, "Registrado localmente (${e.localizedMessage})")
        }
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

        try {
            firebaseAuth.signInWithEmailAndPassword(normalizedEmail, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        saveLocalUser(context, user?.displayName ?: "", normalizedEmail, pass)
                        onResult(true, null)
                    } else {
                        val localSuccess = loginUser(context, normalizedEmail, pass)
                        if (localSuccess) {
                            onResult(true, null)
                        } else {
                            val errorMsg = task.exception?.localizedMessage ?: "Correo o contraseña incorrectos"
                            onResult(false, errorMsg)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    val localSuccess = loginUser(context, normalizedEmail, pass)
                    if (localSuccess) {
                        onResult(true, null)
                    } else {
                        onResult(false, e.localizedMessage)
                    }
                }
        } catch (e: Exception) {
            val localSuccess = loginUser(context, normalizedEmail, pass)
            if (localSuccess) {
                onResult(true, null)
            } else {
                onResult(false, e.localizedMessage)
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

        try {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val email = user?.email ?: ""
                        val name = user?.displayName ?: email.substringBefore("@")
                        saveLocalUser(context, name, email)
                        onResult(true, email, name, null)
                    } else {
                        onResult(false, null, null, task.exception?.localizedMessage)
                    }
                }
                .addOnFailureListener { e ->
                    onResult(false, null, null, e.localizedMessage)
                }
        } catch (e: Exception) {
            onResult(false, null, null, e.localizedMessage)
        }
    }

    fun registerUser(context: Context, name: String, email: String, pass: String): Boolean {
        saveLocalUser(context, name, email, pass)
        return true
    }

    fun loginUser(context: Context, email: String, pass: String): Boolean {
        val prefs = getPrefs(context)
        val normalizedEmail = email.trim().lowercase()

        val savedPass = prefs.getString(KEY_PREFIX_PASS + normalizedEmail, null)
        return savedPass != null && savedPass == pass
    }

    fun getUserName(context: Context, email: String): String? {
        val prefs = getPrefs(context)
        val normalizedEmail = email.trim().lowercase()
        return prefs.getString(KEY_PREFIX_NAME + normalizedEmail, null)
    }

    fun signOut(context: Context) {
        try {
            getSafeAuth(context)?.signOut()
        } catch (_: Exception) {}
    }
}
