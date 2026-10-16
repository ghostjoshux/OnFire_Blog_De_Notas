package com.example.onfire_blog_de_notas.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onfire_blog_de_notas.R
import com.example.onfire_blog_de_notas.ui.theme.OnFire_Blog_De_NotasTheme

@Composable
fun WelcomeScreen(
    onGoogleClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6FE))
    ) {
        // Decorative background fluid shapes
        BackgroundDecoration()

        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Center Illustration & App Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // App Logo Illustration Box
                LogoIllustration()

                Spacer(modifier = Modifier.height(28.dp))

                // App Title "OnFire"
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "On",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B1828)
                    )
                    Text(
                        text = "Fire",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF7E46FF)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "Tus notas, contactos y actividades\nen un solo lugar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF77728B),
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )
            }

            // Buttons Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Button 1: Continuar con Google
                Button(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.05f),
                            spotColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF221F33)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFEFEBF6))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuar con Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF221F33)
                        )
                    }
                }

                // Button 2: Iniciar sesión
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF7E46FF).copy(alpha = 0.3f),
                            spotColor = Color(0xFF7E46FF).copy(alpha = 0.4f)
                        ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8852FF),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Button 3: Crear cuenta
                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, Color(0xFF9868FF)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.7f),
                        contentColor = Color(0xFF8852FF)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Person Icon",
                            tint = Color(0xFF8852FF),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Crear cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8852FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundDecoration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Top Left wave shape
        val path1 = Path().apply {
            moveTo(0f, 0f)
            lineTo(width * 0.65f, 0f)
            cubicTo(
                width * 0.5f, height * 0.08f,
                width * 0.2f, height * 0.12f,
                0f, height * 0.18f
            )
            close()
        }
        drawPath(
            path = path1,
            color = Color(0xFFEDE5FD).copy(alpha = 0.7f)
        )

        // Top Right soft wave shape
        val path2 = Path().apply {
            moveTo(width, height * 0.15f)
            cubicTo(
                width * 0.75f, height * 0.25f,
                width * 0.8f, height * 0.4f,
                width, height * 0.48f
            )
            close()
        }
        drawPath(
            path = path2,
            color = Color(0xFFF3EDFF).copy(alpha = 0.8f)
        )

        // Bottom background wave shape
        val path3 = Path().apply {
            moveTo(0f, height * 0.82f)
            cubicTo(
                width * 0.35f, height * 0.78f,
                width * 0.65f, height * 0.96f,
                width, height * 0.88f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path3,
            color = Color(0xFFEBE0FF).copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun LogoIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(52.dp),
                ambientColor = Color(0xFF7E46FF).copy(alpha = 0.25f),
                spotColor = Color(0xFF7E46FF).copy(alpha = 0.35f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.logo_onfire),
            contentDescription = "Logo OnFire",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(52.dp))
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    OnFire_Blog_De_NotasTheme {
        WelcomeScreen()
    }
}
