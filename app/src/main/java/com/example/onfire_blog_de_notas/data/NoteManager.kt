package com.example.onfire_blog_de_notas.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject

data class NoteItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dueDate: String? = null,
    val isImportant: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "timestamp" to timestamp,
        "dueDate" to (dueDate ?: ""),
        "isImportant" to isImportant
    )

    companion object {
        fun fromMap(map: Map<*, *>): NoteItem {
            val dDate = map["dueDate"] as? String
            return NoteItem(
                id = map["id"] as? String ?: java.util.UUID.randomUUID().toString(),
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                dueDate = if (dDate.isNullOrBlank()) null else dDate,
                isImportant = map["isImportant"] as? Boolean ?: false
            )
        }
    }
}

data class ContactItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phoneOrEmail: String
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "phoneOrEmail" to phoneOrEmail
    )

    companion object {
        fun fromMap(map: Map<*, *>): ContactItem {
            return ContactItem(
                id = map["id"] as? String ?: java.util.UUID.randomUUID().toString(),
                name = map["name"] as? String ?: "",
                phoneOrEmail = map["phoneOrEmail"] as? String ?: ""
            )
        }
    }
}

object NoteManager {
    private const val PREF_NAME = "onfire_notes_prefs"
    private const val KEY_NOTES = "notes_json"
    private const val KEY_CONTACTS = "contacts_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getUserId(): String {
        return try {
            FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
        } catch (_: Throwable) {
            "default_user"
        }
    }

    private fun syncToFirestore(notes: List<NoteItem>, contacts: List<ContactItem>) {
        try {
            val userId = getUserId()
            val db = FirebaseFirestore.getInstance()

            val notesMap = notes.map { it.toMap() }
            db.collection("users").document(userId).collection("notes").document("all_notes")
                .set(mapOf("list" to notesMap))

            val contactsMap = contacts.map { it.toMap() }
            db.collection("users").document(userId).collection("contacts").document("all_contacts")
                .set(mapOf("list" to contactsMap))
        } catch (_: Throwable) {}
    }

    fun getNotes(context: Context): List<NoteItem> {
        val jsonStr = getPrefs(context).getString(KEY_NOTES, null) ?: return emptyList()
        val list = try {
            val array = JSONArray(jsonStr)
            val mutableList = mutableListOf<NoteItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val dDate = if (obj.has("dueDate") && !obj.isNull("dueDate")) obj.getString("dueDate") else null
                mutableList.add(
                    NoteItem(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        description = obj.optString("description"),
                        timestamp = obj.optLong("timestamp"),
                        dueDate = if (dDate.isNullOrBlank()) null else dDate,
                        isImportant = obj.optBoolean("isImportant", false)
                    )
                )
            }
            mutableList
        } catch (e: Exception) {
            emptyList()
        }

        fetchNotesFromFirestore(context)
        return list
    }

    private fun fetchNotesFromFirestore(context: Context) {
        try {
            val userId = getUserId()
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("notes").document("all_notes")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val listData = document.get("list") as? List<*>
                        if (listData != null) {
                            val notes = listData.mapNotNull { item ->
                                (item as? Map<*, *>)?.let { NoteItem.fromMap(it) }
                            }
                            saveNotesLocal(context, notes)
                        }
                    }
                }
        } catch (_: Throwable) {}
    }

    private fun fetchContactsFromFirestore(context: Context) {
        try {
            val userId = getUserId()
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("contacts").document("all_contacts")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val listData = document.get("list") as? List<*>
                        if (listData != null) {
                            val contacts = listData.mapNotNull { item ->
                                (item as? Map<*, *>)?.let { ContactItem.fromMap(it) }
                            }
                            saveContactsLocal(context, contacts)
                        }
                    }
                }
        } catch (_: Throwable) {}
    }

    fun addNote(context: Context, title: String, description: String, dueDate: String?, isImportant: Boolean): List<NoteItem> {
        val current = getNotes(context).toMutableList()
        current.add(0, NoteItem(title = title, description = description, dueDate = dueDate, isImportant = isImportant))
        saveNotes(context, current)
        return current
    }

    fun updateNote(context: Context, id: String, title: String, description: String, dueDate: String?, isImportant: Boolean): List<NoteItem> {
        val current = getNotes(context).toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            current[index] = current[index].copy(
                title = title,
                description = description,
                dueDate = dueDate,
                isImportant = isImportant
            )
            saveNotes(context, current)
        }
        return current
    }

    fun toggleImportant(context: Context, id: String): List<NoteItem> {
        val current = getNotes(context).toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = current[index]
            current[index] = item.copy(isImportant = !item.isImportant)
            saveNotes(context, current)
        }
        return current
    }

    fun deleteNote(context: Context, id: String): List<NoteItem> {
        val current = getNotes(context).toMutableList()
        current.removeAll { it.id == id }
        saveNotes(context, current)
        return current
    }

    private fun saveNotesLocal(context: Context, notes: List<NoteItem>) {
        val array = JSONArray()
        for (note in notes) {
            val obj = JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("description", note.description)
            obj.put("timestamp", note.timestamp)
            obj.put("dueDate", note.dueDate ?: "")
            obj.put("isImportant", note.isImportant)
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_NOTES, array.toString()).apply()
    }

    private fun saveNotes(context: Context, notes: List<NoteItem>) {
        saveNotesLocal(context, notes)
        syncToFirestore(notes, getContacts(context))
    }

    fun getContacts(context: Context): List<ContactItem> {
        val jsonStr = getPrefs(context).getString(KEY_CONTACTS, null) ?: return emptyList()
        val list = try {
            val array = JSONArray(jsonStr)
            val mutableList = mutableListOf<ContactItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                mutableList.add(
                    ContactItem(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        phoneOrEmail = obj.optString("phoneOrEmail")
                    )
                )
            }
            mutableList
        } catch (e: Exception) {
            emptyList()
        }

        fetchContactsFromFirestore(context)
        return list
    }

    fun addContact(context: Context, name: String, phoneOrEmail: String): List<ContactItem> {
        val current = getContacts(context).toMutableList()
        current.add(0, ContactItem(name = name, phoneOrEmail = phoneOrEmail))
        saveContacts(context, current)
        return current
    }

    fun updateContact(context: Context, id: String, name: String, phoneOrEmail: String): List<ContactItem> {
        val current = getContacts(context).toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            current[index] = current[index].copy(name = name, phoneOrEmail = phoneOrEmail)
            saveContacts(context, current)
        }
        return current
    }

    fun deleteContact(context: Context, id: String): List<ContactItem> {
        val current = getContacts(context).toMutableList()
        current.removeAll { it.id == id }
        saveContacts(context, current)
        return current
    }

    private fun saveContactsLocal(context: Context, contacts: List<ContactItem>) {
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

    private fun saveContacts(context: Context, contacts: List<ContactItem>) {
        saveContactsLocal(context, contacts)
        syncToFirestore(getNotes(context), contacts)
    }
}
