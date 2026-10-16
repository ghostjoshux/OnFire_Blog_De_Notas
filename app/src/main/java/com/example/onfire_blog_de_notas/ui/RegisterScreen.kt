package com.example.onfire_blog_de_notas.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onfire_blog_de_notas.data.AuthManager
import com.example.onfire_blog_de_notas.ui.theme.OnFire_Blog_De_NotasTheme

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onAlreadyHaveAccountClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7FD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = "Creá tu cuenta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B162C),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Input 1: Nombre
                CustomInputField(
                    label = "Nombre",
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = "Nombre completo"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Input 2: Correo electrónico
                CustomInputField(
                    label = "Correo electrónico",
                    value = correo,
                    onValueChange = { correo = it },
                    placeholder = "ejemplo@gmail.com",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Input 3: Contraseña
                CustomInputField(
                    label = "Contraseña",
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(14.dp))


                CustomInputField(
                    label = "Confirmar contraseña",
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Helper text
                Text(
                    text = "Usá al menos 6 caracteres.",
                    fontSize = 14.sp,
                    color = Color(0xFF7A758E),
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Button 1: Crear cuenta
                Button(
                    onClick = {
                        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank()) {
                            Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                        } else if (contrasena.length < 6) {
                            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        } else if (contrasena != confirmarContrasena) {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        } else {
                            AuthManager.registerUser(context, nombre, correo, contrasena)
                            Toast.makeText(context, "Se registro correctamente", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF6C4EFF).copy(alpha = 0.3f),
                            spotColor = Color(0xFF6C4EFF).copy(alpha = 0.4f)
                        ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B4EFF),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Button 2: Ya tengo cuenta
                Button(
                    onClick = onAlreadyHaveAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFECE4FB),
                        contentColor = Color(0xFF6B4EFF)
                    )
                ) {
                    Text(
                        text = "Ya tengo cuenta",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B4EFF)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .background(Color.White, shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8D88A0),
                modifier = Modifier.padding(start = 12.dp, top = 6.dp)
            )

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    if (placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFFB5B1C5),
                            fontSize = 15.sp
                        )
                    }
                },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword) KeyboardType.Password else keyboardType
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1B162C),
                    unfocusedTextColor = Color(0xFF1B162C)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    OnFire_Blog_De_NotasTheme {
        RegisterScreen()
    }
}
