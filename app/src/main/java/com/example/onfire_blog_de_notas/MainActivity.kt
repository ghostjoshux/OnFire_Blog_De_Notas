package com.example.onfire_blog_de_notas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import com.example.onfire_blog_de_notas.ui.LoginScreen
import com.example.onfire_blog_de_notas.ui.RegisterScreen
import com.example.onfire_blog_de_notas.ui.WelcomeScreen
import com.example.onfire_blog_de_notas.ui.theme.OnFire_Blog_De_NotasTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

enum class AppScreen {
    WELCOME,
    LOGIN,
    REGISTER,
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
                                    // Fallback if Firebase auth failed
                                    val email = userEmail ?: account.email ?: ""
                                    val name = userName ?: account.displayName ?: email.substringBefore("@")
                                    AuthManager.saveLocalUser(context, name, email)
                                    Toast.makeText(context, "¡Bienvenido/a $name!", Toast.LENGTH_SHORT).show()
                                    currentScreen = AppScreen.HOME
                                }
                            }
                        } else {
                            // Local fallback if idToken is null
                            val email = account?.email ?: ""
                            val name = account?.displayName ?: email.substringBefore("@")
                            AuthManager.saveLocalUser(context, name, email)
                            Toast.makeText(context, "¡Bienvenido/a $name!", Toast.LENGTH_SHORT).show()
                            currentScreen = AppScreen.HOME

                            val webClientId = context.getString(R.string.default_web_client_id)
                            if (webClientId.contains("YOUR_WEB_CLIENT_ID")) {
                                Toast.makeText(
                                    context,
                                    "Nota: Configura el Client ID Web en Firebase Console para sincronizar Firebase.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: ApiException) {
                        when (e.statusCode) {
                            2 -> {
                                Toast.makeText(
                                    context,
                                    "Google Play Services desactualizado en este emulador/dispositivo (Código 2).",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            10, 12500 -> {
                                Toast.makeText(
                                    context,
                                    "Configura el SHA-1 y el Client ID Web en Firebase Console (Error Google ${e.statusCode}).",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            12501 -> {
                                // User cancelled
                            }
                            else -> {
                                Toast.makeText(context, "Error Google (${e.statusCode}): ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
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

                // Handle system back button
                if (currentScreen != AppScreen.WELCOME && currentScreen != AppScreen.HOME) {
                    BackHandler {
                        currentScreen = AppScreen.WELCOME
                    }
                }

                when (currentScreen) {
                    AppScreen.WELCOME -> {
                        WelcomeScreen(
                            onGoogleClick = launchGoogleSignIn,
                            onLoginClick = {
                                currentScreen = AppScreen.LOGIN
                            },
                            onRegisterClick = {
                                currentScreen = AppScreen.REGISTER
                            }
                        )
                    }

                    AppScreen.REGISTER -> {
                        RegisterScreen(
                            onRegisterSuccess = {
                                currentScreen = AppScreen.HOME
                            },
                            onAlreadyHaveAccountClick = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = {
                                currentScreen = AppScreen.HOME
                            },
                            onRegisterClick = {
                                currentScreen = AppScreen.REGISTER
                            },
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
