package com.example.registroflytransportation.ui.screens

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.registroflytransportation.ui.theme.*
import com.example.registroflytransportation.viewModel.FlyTViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterPage(
    viewModel: FlyTViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var savedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage.collectAsState()
    val registrationSuccess by viewModel.registrationSuccess.collectAsState()

    // Crear URI para guardar en galería
    fun createImageUriInGallery(): Uri? {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "FLYT_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FlyT")
                }
            }

            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } catch (e: Exception) {
            Log.e("RegisterPage", "Error creating gallery URI", e)
            null
        }
    }

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            photoUri = tempUri
            savedPhotoUri = tempUri
            Log.d("RegisterPage", "Photo saved to gallery: $savedPhotoUri")
        }
    }

    // Launcher para múltiples permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false

        if (cameraGranted) {
            tempUri = createImageUriInGallery()
            if (tempUri != null) {
                try {
                    cameraLauncher.launch(tempUri!!)
                } catch (e: Exception) {
                    Log.e("RegisterPage", "Error launching camera", e)
                }
            }
        }
    }

    // Dialog de éxito
    if (registrationSuccess) {
        SuccessDialog(
            onDismiss = {
                viewModel.clearRegistrationSuccess()
                onRegisterSuccess()
            }
        )
    }

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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Foto de Perfil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(4.dp, White, CircleShape)
                                .clickable {
                                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        arrayOf(Manifest.permission.CAMERA)
                                    } else {
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    }
                                    permissionLauncher.launch(permissions)
                                },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(White)
                                .border(4.dp, PrimaryBlue, CircleShape)
                                .clickable {
                                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        arrayOf(Manifest.permission.CAMERA)
                                    } else {
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    }
                                    permissionLauncher.launch(permissions)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Tomar foto",
                                    modifier = Modifier.size(48.dp),
                                    tint = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tomar Foto",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (photoUri == null) "Toca para tomar tu foto" else "Foto guardada en galería",
                    fontSize = 12.sp,
                    color = White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre y Apellido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        viewModel.clearError()
                    },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = apellido,
                    onValueChange = {
                        apellido = it
                        viewModel.clearError()
                    },
                    label = { Text("Apellido") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = White
                ),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teléfono y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it
                        viewModel.clearError()
                    },
                    label = { Text("Teléfono") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = {
                        fechaNacimiento = it
                        viewModel.clearError()
                    },
                    label = { Text("Fecha Nac.") },
                    placeholder = { Text("DD/MM/YYYY") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contraseñas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.clearError()
                    },
                    label = { Text("Contraseña") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                OutlinedTextField(
                    value = confirmarPassword,
                    onValueChange = {
                        confirmarPassword = it
                        viewModel.clearError()
                    },
                    label = { Text("Confirmar") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
            }

            // Errores
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Crear Cuenta
            Button(
                onClick = {
                    // Llamar al ViewModel para validar y registrar
                    viewModel.validateAndRegisterUser(
                        name = nombre,
                        lastName = apellido,
                        email = email,
                        phoneNumber = telefono,
                        birthday = fechaNacimiento,
                        password = password,
                        confirmPassword = confirmarPassword,
                        photoUri = savedPhotoUri?.toString() ?: ""
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.clearError()
                    onBackToLogin()
                }
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = White,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { value, _ ->
            scale = value
        }
        delay(2000)
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .scale(scale),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    modifier = Modifier.size(80.dp),
                    tint = SuccessGreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Registro Exitoso!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu cuenta ha sido creada correctamente",
                    fontSize = 14.sp,
                    color = DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}