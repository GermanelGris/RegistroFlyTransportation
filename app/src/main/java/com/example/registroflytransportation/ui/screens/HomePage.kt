package com.example.registroflytransportation.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.registroflytransportation.ui.theme.*
import com.example.registroflytransportation.viewModel.FlyTViewModel

@Composable
fun HomePage(
    viewModel: FlyTViewModel,
    userName: String,
    onLogout: () -> Unit
) {
    val searchFlightState by viewModel.searchFlightState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BlueStart, PurpleEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con foto de perfil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Foto de perfil
                    if (currentUser?.photoUri?.isNotEmpty() == true) {
                        Image(
                            painter = rememberAsyncImagePainter(Uri.parse(currentUser?.photoUri)),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Icono por defecto si no hay foto
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(White)
                                .border(2.dp, PrimaryBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Usuario",
                                modifier = Modifier.size(32.dp),
                                tint = PrimaryBlue
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "FLY T",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = "Hola, $userName",
                            fontSize = 14.sp,
                            color = White.copy(alpha = 0.9f)
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        viewModel.clearSearchForm()
                        viewModel.clearError()
                        onLogout()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("Salir")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vuela hacia tus sueños",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Descubre el mundo con Fly Transportation (FlyT). Ofertas exclusivas y el mejor servicio te esperan.",
                    fontSize = 16.sp,
                    color = White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card de búsqueda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Buscar Vuelos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Origen
                    OutlinedTextField(
                        value = searchFlightState.origen,
                        onValueChange = {
                            viewModel.updateSearchField("origen", it)
                            viewModel.clearError()
                        },
                        label = { Text("Origen") },
                        placeholder = { Text("Santiago") },

                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Destino
                    OutlinedTextField(
                        value = searchFlightState.destino,
                        onValueChange = {
                            viewModel.updateSearchField("destino", it)
                            viewModel.clearError()
                        },
                        label = { Text("Destino") },
                        placeholder = { Text("Buenos Aires") },

                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fechas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchFlightState.fechaSalida,
                            onValueChange = {
                                viewModel.updateSearchField("fechaSalida", it)
                                viewModel.clearError()
                            },
                            label = { Text("Fecha Salida") },
                            placeholder = { Text("30-10-2025") },

                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = searchFlightState.fechaRegreso,
                            onValueChange = {
                                viewModel.updateSearchField("fechaRegreso", it)
                            },
                            label = { Text("Regreso") },
                            placeholder = { Text("05-11-2025") },

                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pasajeros
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchFlightState.numAdultos,
                            onValueChange = {
                                if (it.isEmpty() || it.toIntOrNull() != null) {
                                    viewModel.updateSearchField("numAdultos", it)
                                }
                            },
                            label = { Text("Adultos") },

                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = searchFlightState.numNinos,
                            onValueChange = {
                                if (it.isEmpty() || it.toIntOrNull() != null) {
                                    viewModel.updateSearchField("numNinos", it)
                                }
                            },
                            label = { Text("Niños") },

                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            ),
                            singleLine = true
                        )
                    }

                    // Mensaje de error
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = ErrorRed,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Buscar
                    Button(
                        onClick = {
                            val success = viewModel.searchFlights()
                            if (success) {
                                // Aquí navegarías a la pantalla de resultados
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buscar Vuelos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}