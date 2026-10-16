package com.example.onfire_blog_de_notas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.onfire_blog_de_notas.ui.LoginScreen
import com.example.onfire_blog_de_notas.ui.RegisterScreen
import com.example.onfire_blog_de_notas.ui.WelcomeScreen
import com.example.onfire_blog_de_notas.ui.theme.OnFire_Blog_De_NotasTheme

enum class AppScreen {
    WELCOME,
    LOGIN,
    REGISTER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnFire_Blog_De_NotasTheme {
                val context = LocalContext.current
                var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }

                // Handle system back button to return to Welcome screen
                if (currentScreen != AppScreen.WELCOME) {
                    BackHandler {
                        currentScreen = AppScreen.WELCOME
                    }
                }

                when (currentScreen) {
                    AppScreen.WELCOME -> {
                        WelcomeScreen(
                            onGoogleClick = {
                                Toast.makeText(context, "Continuar con Google", Toast.LENGTH_SHORT).show()
                            },
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
                                currentScreen = AppScreen.LOGIN
                            },
                            onAlreadyHaveAccountClick = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = {
                                // Successful login action
                                currentScreen = AppScreen.WELCOME
                            },
                            onRegisterClick = {
                                currentScreen = AppScreen.REGISTER
                            }
                        )
                    }
                }
            }
        }
    }
}
