package com.example.registroflytransportation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registroflytransportation.ui.screens.HomePage
import com.example.registroflytransportation.ui.screens.LoginPage
import com.example.registroflytransportation.ui.screens.RegisterPage
import com.example.registroflytransportation.ui.theme.RegistroFlyTransportationTheme
import com.example.registroflytransportation.viewModel.FlyTViewModel

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
    val viewModel: FlyTViewModel = viewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    // Navegación basada en el estado de login
    LaunchedEffect(isLoggedIn) {
        currentScreen = if (isLoggedIn) Screen.Home else Screen.Login
    }

    when (currentScreen) {
        is Screen.Login -> {
            LoginPage(
                viewModel = viewModel,
                onLoginSuccess = {
                    currentScreen = Screen.Home
                },
                onNavigateToRegister = {
                    currentScreen = Screen.Register
                }
            )
        }
        is Screen.Register -> {
            RegisterPage(
                viewModel = viewModel,
                onRegisterSuccess = {
                    currentScreen = Screen.Login
                },
                onBackToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }
        is Screen.Home -> {
            HomePage(
                viewModel = viewModel,
                userName = currentUser?.name ?: "Usuario",
                onLogout = {
                    viewModel.logout()
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