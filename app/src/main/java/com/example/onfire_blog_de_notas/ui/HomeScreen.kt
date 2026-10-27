package com.example.onfire_blog_de_notas.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onfire_blog_de_notas.data.AuthManager
import com.example.onfire_blog_de_notas.data.ContactItem
import com.example.onfire_blog_de_notas.data.NoteItem
import com.example.onfire_blog_de_notas.data.NoteManager

@Composable
fun HomeScreen(
    onSignOutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser = try { AuthManager.currentUser } catch (_: Throwable) { null }
    val userEmail = currentUser?.email ?: ""
    val rawName = currentUser?.displayName ?: AuthManager.getUserName(context, userEmail) ?: "Fio"
    val displayName = if (rawName.isNotBlank()) rawName.trim().substringBefore(" ") else "Fio"

    var notesList by remember { mutableStateOf(NoteManager.getNotes(context)) }
    var contactsList by remember { mutableStateOf(NoteManager.getContacts(context)) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Inicio, 1: Notas, 2: Contactos, 3: Cuenta

    var showCreateNoteDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom navigation bar
        ) {
            when (selectedTab) {
                0 -> {
                    // Inicio / Dashboard Tab (Exact screenshot layout)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Breadcrumb Header: NEXO / ACCESO
                            Text(
                                text = "NEXO / ACCESO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8D7BB0),
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Main Title: Hola, Fio
                            Text(
                                text = "Hola, $displayName",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B162C)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Summary Card
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
                                colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                        color = Color(0xFF1B162C)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${notesList.size} notas guardadas · ${contactsList.size} contactos",
                                        fontSize = 14.sp,
                                        color = Color(0xFF7A758E)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Button 1: + Crear nota
                            Button(
                                onClick = { showCreateNoteDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(18.dp),
                                        ambientColor = Color(0xFF634CAE).copy(alpha = 0.3f),
                                        spotColor = Color(0xFF634CAE).copy(alpha = 0.4f)
                                    ),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF634CAE),
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

                            // Button 2: + Agregar contacto
                            Button(
                                onClick = { showAddContactDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFECE4FB),
                                    contentColor = Color(0xFF634CAE)
                                )
                            ) {
                                Text(
                                    text = "+ Agregar contacto",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF634CAE)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Section Title: Notas recientes
                            Text(
                                text = "Notas recientes",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B162C)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // List of Recent Notes
                        if (notesList.isEmpty()) {
                            item {
                                Text(
                                    text = "No tienes notas creadas aún.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF7A758E),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        } else {
                            items(notesList) { note ->
                                NoteCardItem(note = note)
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
                                color = Color(0xFF1B162C)
                            )
                            Button(
                                onClick = { showCreateNoteDialog = true },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF634CAE))
                            ) {
                                Text("+ Nueva", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notesList) { note ->
                                NoteCardItem(note = note)
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
                                color = Color(0xFF1B162C)
                            )
                            Button(
                                onClick = { showAddContactDialog = true },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF634CAE))
                            ) {
                                Text("+ Nuevo", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(contactsList) { contact ->
                                ContactCardItem(contact = contact)
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
                                .background(Color(0xFFECE4FB), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.take(1).uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF634CAE)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = rawName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B162C)
                        )

                        Text(
                            text = if (userEmail.isNotEmpty()) userEmail else "Cuenta con Google",
                            fontSize = 14.sp,
                            color = Color(0xFF7A758E)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Estado de sesión",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1B162C)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Sincronizado con Firebase",
                                    fontSize = 14.sp,
                                    color = Color(0xFF7A758E)
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
                                containerColor = Color(0xFFECE4FB),
                                contentColor = Color(0xFF634CAE)
                            )
                        ) {
                            Text(
                                text = "Cerrar sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF634CAE)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        // Floating Bottom Navigation Bar (Matching exact screenshot styling)
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
                colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                color = if (isSelected) Color(0xFF634CAE) else Color(0xFF9A95AA)
                            )
                        }
                    }
                }
            }
        }

        // Dialog 1: Dialog for creating a new Note
        if (showCreateNoteDialog) {
            var titleInput by remember { mutableStateOf("") }
            var descInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showCreateNoteDialog = false },
                title = {
                    Text(
                        text = "Crear nueva nota",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B162C)
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
                                focusedBorderColor = Color(0xFF634CAE),
                                focusedLabelColor = Color(0xFF634CAE)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Descripción") },
                            placeholder = { Text("Ej. Definir las pantallas con Joshua.") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF634CAE),
                                focusedLabelColor = Color(0xFF634CAE)
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF634CAE))
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateNoteDialog = false }) {
                        Text("Cancelar", color = Color(0xFF7A758E))
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Dialog 2: Dialog for adding a new Contact
        if (showAddContactDialog) {
            var nameInput by remember { mutableStateOf("") }
            var detailInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                title = {
                    Text(
                        text = "Agregar contacto",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B162C)
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nombre") },
                            placeholder = { Text("Ej. Joshua") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF634CAE),
                                focusedLabelColor = Color(0xFF634CAE)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = detailInput,
                            onValueChange = { detailInput = it },
                            label = { Text("Teléfono / Correo") },
                            placeholder = { Text("Ej. joshua@ejemplo.com") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF634CAE),
                                focusedLabelColor = Color(0xFF634CAE)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                contactsList = NoteManager.addContact(context, nameInput.trim(), detailInput.trim())
                                Toast.makeText(context, "Contacto agregado con éxito", Toast.LENGTH_SHORT).show()
                                showAddContactDialog = false
                            } else {
                                Toast.makeText(context, "Ingresa el nombre del contacto", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF634CAE))
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContactDialog = false }) {
                        Text("Cancelar", color = Color(0xFF7A758E))
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun NoteCardItem(note: NoteItem) {
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B162C)
            )
            if (note.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.description,
                    fontSize = 14.sp,
                    color = Color(0xFF7A758E)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun ContactCardItem(contact: ContactItem) {
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFECE4FB), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF634CAE)
                )
            }
            Spacer(modifier = Modifier.padding(start = 14.dp))
            Column {
                Text(
                    text = contact.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B162C)
                )
                if (contact.phoneOrEmail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contact.phoneOrEmail,
                        fontSize = 13.sp,
                        color = Color(0xFF7A758E)
                    )
                }
            }
        }
    }
}
