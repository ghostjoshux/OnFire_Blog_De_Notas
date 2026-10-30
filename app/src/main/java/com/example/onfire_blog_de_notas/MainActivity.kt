package com.example.onfire_blog_de_notas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.onfire_blog_de_notas.data.AuthManager
import com.example.onfire_blog_de_notas.ui.HomeScreen
import com.example.onfire_blog_de_notas.ui.WelcomeScreen
import com.example.onfire_blog_de_notas.ui.theme.OnFire_Blog_De_NotasTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

enum class AppScreen {
    WELCOME,
    HOME
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnFire_Blog_De_NotasTheme {
                val context = LocalContext.current
                val initialScreen = if (AuthManager.currentUser != null) AppScreen.HOME else AppScreen.WELCOME
                var currentScreen by remember { mutableStateOf(initialScreen) }

                // Launcher for Google Sign-In
                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    try {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        val account = task.getResult(ApiException::class.java)
                        val idToken: String? = account?.idToken

                        if (idToken != null && idToken.isNotBlank()) {
                            val credential = GoogleAuthProvider.getCredential(idToken, null)
                            AuthManager.signInWithGoogleCredential(context, credential) { success, userEmail, userName, errorMessage ->
                                if (success) {
                                    Toast.makeText(context, "¡Bienvenido/a ${userName ?: userEmail}!", Toast.LENGTH_SHORT).show()
                                    currentScreen = AppScreen.HOME
                                } else {
                                    Toast.makeText(context, errorMessage ?: "Error de autenticación con Firebase", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Error: No se obtuvo el token de Google", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(
                            context,
                            "Error al iniciar sesión con Google (${e.statusCode}). Verifica la configuración de Firebase y SHA-1.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }

                val launchGoogleSignIn: () -> Unit = {
                    try {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut().addOnCompleteListener {
                            try {
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error al abrir inicio con Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al iniciar Google Sign-In: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }

                when (currentScreen) {
                    AppScreen.WELCOME -> {
                        WelcomeScreen(
                            onGoogleClick = launchGoogleSignIn
                        )
                    }

                    AppScreen.HOME -> {
                        HomeScreen(
                            onSignOutClick = {
                                currentScreen = AppScreen.WELCOME
                            }
                        )
                    }
                }
            }
        }
    }
}
