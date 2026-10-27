package com.example.onfire_blog_de_notas.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class NoteItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ContactItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phoneOrEmail: String
)

object NoteManager {
    private const val PREF_NAME = "onfire_notes_prefs"
    private const val KEY_NOTES = "notes_json"
    private const val KEY_CONTACTS = "contacts_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getNotes(context: Context): List<NoteItem> {
        val jsonStr = getPrefs(context).getString(KEY_NOTES, null) ?: return getInitialNotes(context)
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<NoteItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    NoteItem(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        description = obj.optString("description"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
            if (list.isEmpty()) getInitialNotes(context) else list
        } catch (e: Exception) {
            getInitialNotes(context)
        }
    }

    fun addNote(context: Context, title: String, description: String): List<NoteItem> {
        val current = getNotes(context).toMutableList()
        current.add(0, NoteItem(title = title, description = description))
        saveNotes(context, current)
        return current
    }

    private fun saveNotes(context: Context, notes: List<NoteItem>) {
        val array = JSONArray()
        for (note in notes) {
            val obj = JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("description", note.description)
            obj.put("timestamp", note.timestamp)
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_NOTES, array.toString()).apply()
    }

    private fun getInitialNotes(context: Context): List<NoteItem> {
        val initial = listOf(
            NoteItem(
                title = "Reunión de proyecto",
                description = "Definir las pantallas con Joshua."
            )
        )
        saveNotes(context, initial)
        return initial
    }

    fun getContacts(context: Context): List<ContactItem> {
        val jsonStr = getPrefs(context).getString(KEY_CONTACTS, null) ?: return getInitialContacts(context)
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ContactItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ContactItem(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        phoneOrEmail = obj.optString("phoneOrEmail")
                    )
                )
            }
            if (list.isEmpty()) getInitialContacts(context) else list
        } catch (e: Exception) {
            getInitialContacts(context)
        }
    }

    fun addContact(context: Context, name: String, phoneOrEmail: String): List<ContactItem> {
        val current = getContacts(context).toMutableList()
        current.add(0, ContactItem(name = name, phoneOrEmail = phoneOrEmail))
        saveContacts(context, current)
        return current
    }

    private fun saveContacts(context: Context, contacts: List<ContactItem>) {
        val array = JSONArray()
        for (contact in contacts) {
            val obj = JSONObject()
            obj.put("id", contact.id)
            obj.put("name", contact.name)
            obj.put("phoneOrEmail", contact.phoneOrEmail)
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_CONTACTS, array.toString()).apply()
    }

    private fun getInitialContacts(context: Context): List<ContactItem> {
        val initial = listOf(
            ContactItem(name = "Joshua", phoneOrEmail = "joshua@ejemplo.com"),
            ContactItem(name = "María", phoneOrEmail = "+51 987654321"),
            ContactItem(name = "Carlos", phoneOrEmail = "carlos@ejemplo.com")
        )
        saveContacts(context, initial)
        return initial
    }
}
