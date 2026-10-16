package com.example.onfire_blog_de_notas.data

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "onfire_user_prefs"
    private const val KEY_PREFIX_PASS = "user_pass_"
    private const val KEY_PREFIX_NAME = "user_name_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun registerUser(context: Context, name: String, email: String, pass: String): Boolean {
        val prefs = getPrefs(context)
        val normalizedEmail = email.trim().lowercase()

        prefs.edit()
            .putString(KEY_PREFIX_PASS + normalizedEmail, pass)
            .putString(KEY_PREFIX_NAME + normalizedEmail, name.trim())
            .apply()

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
}
