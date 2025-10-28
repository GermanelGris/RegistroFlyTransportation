package com.example.registroflytransportation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.registroflytransportation.ui.theme.RegistroFlyTransportationTheme
import com.example.registroflytransportation.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistroFlyTransportationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlyTApp()
                }
            }
        }
    }
}

@Composable
fun FlyTApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        is Screen.Login -> {
            LoginPage(
                onLoginClick = { usuario: String, password: String ->
                    // Validar credenciales
                    if (usuario == "admin" && password == "123456") {
                        currentUser = usuario
                        isLoggedIn = true
                        currentScreen = Screen.Home
                    } else {
                        // Credenciales incorrectas
                        // Aquí podrías mostrar un mensaje de error
                    }
                },
                onRegisterClick = {
                    currentScreen = Screen.Register
                }
            )
        }
        is Screen.Register -> {
            RegisterPage(
                onRegisterComplete = {
                    currentScreen = Screen.Login
                },
                onBackToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }
        is Screen.Home -> {
            HomePage(
                userName = currentUser ?: "",
                onLogout = {
                    isLoggedIn = false
                    currentUser = null
                    currentScreen = Screen.Login
                }
            )
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Home : Screen()
}