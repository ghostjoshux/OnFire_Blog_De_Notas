package com.example.onfire_blog_de_notas.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onfire_blog_de_notas.data.AuthManager
import com.example.onfire_blog_de_notas.data.ContactItem
import com.example.onfire_blog_de_notas.data.NoteItem
import com.example.onfire_blog_de_notas.data.NoteManager

data class CountryCode(val country: String, val code: String)

val countryCodes = listOf(
    CountryCode("Perú", "+51"),
    CountryCode("México", "+52"),
    CountryCode("Argentina", "+54"),
    CountryCode("Colombia", "+57"),
    CountryCode("España", "+34"),
    CountryCode("EE. UU.", "+1"),
    CountryCode("Chile", "+56"),
    CountryCode("Venezuela", "+58"),
    CountryCode("Ecuador", "+593"),
    CountryCode("Bolivia", "+591"),
    CountryCode("Brasil", "+55"),
    CountryCode("Costa Rica", "+506"),
    CountryCode("Cuba", "+53"),
    CountryCode("El Salvador", "+503"),
    CountryCode("Guatemala", "+502"),
    CountryCode("Honduras", "+504"),
    CountryCode("Nicaragua", "+505"),
    CountryCode("Panamá", "+507"),
    CountryCode("Paraguay", "+595"),
    CountryCode("Uruguay", "+598")
)

fun parsePhone(phoneOrEmail: String): Pair<String, String> {
    val trimmed = phoneOrEmail.trim()
    for (cc in countryCodes) {
        if (trimmed.startsWith(cc.code)) {
            val numberPart = trimmed.removePrefix(cc.code).trim().filter { it.isDigit() }
            return Pair(cc.code, numberPart)
        }
    }
    return Pair("+51", trimmed.filter { it.isDigit() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF8F6FC)
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color(0xFFE6E1E5) else Color(0xFF1B162C)
    val secondaryTextColor = if (isDark) Color(0xFFCAC4D0) else Color(0xFF7A758E)
    val accentColor = if (isDark) Color(0xFF9B88FF) else Color(0xFF634CAE)
    val secondaryContainerColor = if (isDark) Color(0xFF382E56) else Color(0xFFECE4FB)

    val currentUser = try { AuthManager.currentUser } catch (_: Throwable) { null }
    val userEmail = currentUser?.email ?: ""
    val rawName = currentUser?.displayName ?: AuthManager.getUserName(context, userEmail) ?: "Fio"
    val displayName = if (rawName.isNotBlank()) rawName.trim().substringBefore(" ") else "Fio"

    var notesList by remember { mutableStateOf(NoteManager.getNotes(context)) }
    var contactsList by remember { mutableStateOf(NoteManager.getContacts(context)) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Inicio, 1: Notas, 2: Contactos, 3: Cuenta

    var showCreateNoteDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    var editingNote by remember { mutableStateOf<NoteItem?>(null) }
    var editingContact by remember { mutableStateOf<ContactItem?>(null) }
    var selectedContactAction by remember { mutableStateOf<ContactItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom navigation bar
        ) {
            when (selectedTab) {
                0 -> {
                    // Inicio / Dashboard Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Bienvenido",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondaryTextColor,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Hola, $displayName",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(22.dp),
                                        ambientColor = Color.Black.copy(alpha = 0.03f),
                                        spotColor = Color.Black.copy(alpha = 0.05f)
                                    ),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 22.dp, vertical = 20.dp)
                                ) {
                                    Text(
                                        text = "Tu día, más simple",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${notesList.size} notas guardadas · ${contactsList.size} contactos",
                                        fontSize = 14.sp,
                                        color = secondaryTextColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showCreateNoteDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(18.dp),
                                        ambientColor = accentColor.copy(alpha = 0.3f),
                                        spotColor = accentColor.copy(alpha = 0.4f)
                                    ),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "+ Crear nota",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showAddContactDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = secondaryContainerColor,
                                    contentColor = accentColor
                                )
                            ) {
                                Text(
                                    text = "+ Agregar contacto",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Notas recientes",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (notesList.isEmpty()) {
                            item {
                                Text(
                                    text = "No tienes notas creadas aún.",
                                    fontSize = 14.sp,
                                    color = secondaryTextColor,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        } else {
                            items(notesList) { note ->
                                NoteCardItem(
                                    note = note,
                                    cardColor = cardColor,
                                    textColor = textColor,
                                    secondaryTextColor = secondaryTextColor,
                                    accentColor = accentColor,
                                    onEdit = { editingNote = note },
                                    onDelete = {
                                        notesList = NoteManager.deleteNote(context, note.id)
                                        Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                1 -> {
                    // Tab 1: Full Notes Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Todas las Notas",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )
                            Button(
                                onClick = { showCreateNoteDialog = true },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                            ) {
                                Text("+ Nueva", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notesList) { note ->
                                NoteCardItem(
                                    note = note,
                                    cardColor = cardColor,
                                    textColor = textColor,
                                    secondaryTextColor = secondaryTextColor,
                                    accentColor = accentColor,
                                    onEdit = { editingNote = note },
                                    onDelete = {
                                        notesList = NoteManager.deleteNote(context, note.id)
                                        Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Tab 2: Contacts Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mis Contactos",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )
                            Button(
                                onClick = { showAddContactDialog = true },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                            ) {
                                Text("+ Nuevo", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(contactsList) { contact ->
                                ContactCardItem(
                                    contact = contact,
                                    cardColor = cardColor,
                                    textColor = textColor,
                                    secondaryTextColor = secondaryTextColor,
                                    accentColor = accentColor,
                                    secondaryContainerColor = secondaryContainerColor,
                                    onClick = { selectedContactAction = contact },
                                    onEdit = { editingContact = contact },
                                    onDelete = {
                                        contactsList = NoteManager.deleteContact(context, contact.id)
                                        Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // Tab 3: Account Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(36.dp))

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(secondaryContainerColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.take(1).uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = rawName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Text(
                            text = if (userEmail.isNotEmpty()) userEmail else "Cuenta con Google",
                            fontSize = 14.sp,
                            color = secondaryTextColor
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Estado de sesión",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Sincronizado con Firebase",
                                    fontSize = 14.sp,
                                    color = secondaryTextColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                AuthManager.signOut(context)
                                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                                onSignOutClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryContainerColor,
                                contentColor = accentColor
                            )
                        ) {
                            Text(
                                text = "Cerrar sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        // Floating Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color.Black.copy(alpha = 0.06f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf("Inicio", "Notas", "Contactos", "Cuenta")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else secondaryTextColor
                            )
                        }
                    }
                }
            }
        }

        // Dialog: Create Note
        if (showCreateNoteDialog) {
            var titleInput by remember { mutableStateOf("") }
            var descInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showCreateNoteDialog = false },
                title = {
                    Text(
                        text = "Crear nueva nota",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Título") },
                            placeholder = { Text("Ej. Reunión de proyecto") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Descripción") },
                            placeholder = { Text("Ej. Ir al supermercado") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                notesList = NoteManager.addNote(context, titleInput.trim(), descInput.trim())
                                Toast.makeText(context, "Nota creada con éxito", Toast.LENGTH_SHORT).show()
                                showCreateNoteDialog = false
                            } else {
                                Toast.makeText(context, "Ingresa un título para la nota", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateNoteDialog = false }) {
                        Text("Cancelar", color = secondaryTextColor)
                    }
                },
                containerColor = cardColor,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Dialog: Edit Note
        editingNote?.let { note ->
            var titleInput by remember { mutableStateOf(note.title) }
            var descInput by remember { mutableStateOf(note.description) }

            AlertDialog(
                onDismissRequest = { editingNote = null },
                title = {
                    Text(
                        text = "Editar nota",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Título") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Descripción") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                notesList = NoteManager.updateNote(context, note.id, titleInput.trim(), descInput.trim())
                                Toast.makeText(context, "Nota actualizada", Toast.LENGTH_SHORT).show()
                                editingNote = null
                            } else {
                                Toast.makeText(context, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Actualizar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingNote = null }) {
                        Text("Cancelar", color = secondaryTextColor)
                    }
                },
                containerColor = cardColor,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Dialog: Add Contact
        if (showAddContactDialog) {
            var nameInput by remember { mutableStateOf("") }
            var selectedCode by remember { mutableStateOf("+51") }
            var phoneInput by remember { mutableStateOf("") }
            var expandedCode by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                title = {
                    Text(
                        text = "Agregar contacto",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nombre") },
                            placeholder = { Text("Ej. Jorge") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expandedCode,
                                onExpandedChange = { expandedCode = !expandedCode },
                                modifier = Modifier.width(120.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedCode,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Código") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCode) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = accentColor,
                                        focusedLabelColor = accentColor
                                    ),
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedCode,
                                    onDismissRequest = { expandedCode = false }
                                ) {
                                    countryCodes.forEach { cc ->
                                        DropdownMenuItem(
                                            text = { Text("${cc.code} (${cc.country})") },
                                            onClick = {
                                                selectedCode = cc.code
                                                expandedCode = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) {
                                        phoneInput = input
                                    }
                                },
                                label = { Text("Teléfono") },
                                placeholder = { Text("987654321") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                val fullPhone = if (phoneInput.isNotBlank()) "$selectedCode $phoneInput" else ""
                                contactsList = NoteManager.addContact(context, nameInput.trim(), fullPhone)
                                Toast.makeText(context, "Contacto agregado con éxito", Toast.LENGTH_SHORT).show()
                                showAddContactDialog = false
                            } else {
                                Toast.makeText(context, "Ingresa el nombre del contacto", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContactDialog = false }) {
                        Text("Cancelar", color = secondaryTextColor)
                    }
                },
                containerColor = cardColor,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Dialog: Edit Contact
        editingContact?.let { contact ->
            var nameInput by remember { mutableStateOf(contact.name) }
            val parsed = parsePhone(contact.phoneOrEmail)
            var selectedCode by remember { mutableStateOf(parsed.first) }
            var phoneInput by remember { mutableStateOf(parsed.second) }
            var expandedCode by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { editingContact = null },
                title = {
                    Text(
                        text = "Editar contacto",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nombre") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expandedCode,
                                onExpandedChange = { expandedCode = !expandedCode },
                                modifier = Modifier.width(120.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedCode,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Código") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCode) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = accentColor,
                                        focusedLabelColor = accentColor
                                    ),
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedCode,
                                    onDismissRequest = { expandedCode = false }
                                ) {
                                    countryCodes.forEach { cc ->
                                        DropdownMenuItem(
                                            text = { Text("${cc.code} (${cc.country})") },
                                            onClick = {
                                                selectedCode = cc.code
                                                expandedCode = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) {
                                        phoneInput = input
                                    }
                                },
                                label = { Text("Teléfono") },
                                placeholder = { Text("987654321") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                val fullPhone = if (phoneInput.isNotBlank()) "$selectedCode $phoneInput" else ""
                                contactsList = NoteManager.updateContact(context, contact.id, nameInput.trim(), fullPhone)
                                Toast.makeText(context, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                                editingContact = null
                            } else {
                                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Actualizar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingContact = null }) {
                        Text("Cancelar", color = secondaryTextColor)
                    }
                },
                containerColor = cardColor,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Dialog: Contact Actions (Call, WhatsApp - removed system contacts save)
        selectedContactAction?.let { contact ->
            AlertDialog(
                onDismissRequest = { selectedContactAction = null },
                title = {
                    Text(
                        text = contact.name,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (contact.phoneOrEmail.isNotBlank()) {
                            Text(
                                text = contact.phoneOrEmail,
                                fontSize = 15.sp,
                                color = secondaryTextColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Option 1: Call
                        Button(
                            onClick = {
                                selectedContactAction = null
                                val phoneStr = contact.phoneOrEmail.trim()
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneStr"))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "No se pudo abrir el marcador", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("📞 Llamar por teléfono", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        // Option 2: WhatsApp
                        Button(
                            onClick = {
                                selectedContactAction = null
                                val rawPhone = contact.phoneOrEmail.filter { it.isDigit() }
                                val url = "https://wa.me/$rawPhone"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "WhatsApp no está instalado o no es válido", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White)
                        ) {
                            Text("💬 Escribir por WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { selectedContactAction = null }) {
                        Text("Cancelar", color = secondaryTextColor)
                    }
                },
                containerColor = cardColor,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun NoteCardItem(
    note: NoteItem,
    cardColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.02f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (note.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.description,
                        fontSize = 14.sp,
                        color = secondaryTextColor
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nota",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar nota",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCardItem(
    contact: ContactItem,
    cardColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    secondaryContainerColor: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.02f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(secondaryContainerColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.take(1).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = contact.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    if (contact.phoneOrEmail.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = contact.phoneOrEmail,
                            fontSize = 13.sp,
                            color = secondaryTextColor
                        )
                    }
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar contacto",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar contacto",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
