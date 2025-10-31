package com.example.registroflytransportation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroflytransportation.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlyTViewModel : ViewModel() {

    // Estado de autenticación
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Usuario actual
    private val _currentUser = MutableStateFlow<Users?>(null)
    val currentUser: StateFlow<Users?> = _currentUser.asStateFlow()

    // Lista de usuarios registrados
    private val _registeredUsers = MutableStateFlow<List<Users>>(emptyList())
    val registeredUsers: StateFlow<List<Users>> = _registeredUsers.asStateFlow()

    // Estado del formulario de búsqueda de vuelos
    private val _searchFlightState = MutableStateFlow(SearchFlightForm())
    val searchFlightState: StateFlow<SearchFlightForm> = _searchFlightState.asStateFlow()

    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Inicializar con usuario admin por defecto
    init {
        _registeredUsers.value = listOf(
            Users(
                name = "Admin",
                lastName = "Sistema",
                email = "admin@flyt.com",
                password = "123456",
                phoneNumber = "",
                birthday = ""
            )
        )
    }

    // Login
    fun login(username: String, password: String): Boolean {
        // Buscar usuario por nombre (name) en la lista de registrados
        val user = _registeredUsers.value.find {
            it.name.equals(username, ignoreCase = true) && it.password == password
        }

        // También permitir login con username "admin"
        val isAdmin = username.equals("admin", ignoreCase = true) && password == "123456"

        return if (user != null || isAdmin) {
            _isLoggedIn.value = true
            _currentUser.value = user ?: _registeredUsers.value.first()
            _errorMessage.value = null
            true
        } else {
            _errorMessage.value = "Usuario o contraseña incorrectos"
            false
        }
    }

    // Logout
    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _currentUser.value = null
            _searchFlightState.value = SearchFlightForm()
            _errorMessage.value = null
        }
    }

    // Registrar nuevo usuario
    fun registerUser(user: Users): Boolean {
        // Validar que el email no esté registrado
        if (_registeredUsers.value.any { it.email == user.email }) {
            _errorMessage.value = "El correo electrónico ya está registrado"
            return false
        }

        // Validar campos obligatorios
        if (user.name.isBlank() || user.lastName.isBlank() ||
            user.email.isBlank() || user.password.isBlank()) {
            _errorMessage.value = "Por favor completa todos los campos obligatorios"
            return false
        }

        viewModelScope.launch {
            _registeredUsers.value = _registeredUsers.value.toMutableList().apply {
                add(user)
            }
            _errorMessage.value = null
        }
        return true
    }

    // Actualizar campo del formulario de búsqueda
    fun updateSearchField(field: String, value: String) {
        _searchFlightState.value = when (field) {
            "origen" -> _searchFlightState.value.copy(origen = value)
            "destino" -> _searchFlightState.value.copy(destino = value)
            "fechaSalida" -> _searchFlightState.value.copy(fechaSalida = value)
            "fechaRegreso" -> _searchFlightState.value.copy(fechaRegreso = value)
            "numAdultos" -> _searchFlightState.value.copy(numAdultos = value)
            "numNinos" -> _searchFlightState.value.copy(numNinos = value)
            else -> _searchFlightState.value
        }
    }

    // Buscar vuelos
    fun searchFlights(): Boolean {
        val form = _searchFlightState.value

        if (form.origen.isBlank() || form.destino.isBlank() ||
            form.fechaSalida.isBlank()) {
            _errorMessage.value = "Por favor completa origen, destino y fecha de salida"
            return false
        }

        _errorMessage.value = null
        return true
    }

    // Limpiar mensaje de error
    fun clearError() {
        _errorMessage.value = null
    }

    // Limpiar formulario de búsqueda
    fun clearSearchForm() {
        _searchFlightState.value = SearchFlightForm()
    }
}

// Data class para el formulario de búsqueda de vuelos
data class SearchFlightForm(
    val origen: String = "",
    val destino: String = "",
    val fechaSalida: String = "",
    val fechaRegreso: String = "",
    val numAdultos: String = "1",
    val numNinos: String = "0"
)