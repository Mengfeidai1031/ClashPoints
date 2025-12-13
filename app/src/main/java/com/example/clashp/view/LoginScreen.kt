package com.example.clashp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clashp.ui.components.PrimaryClashButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onPlayAsGuest: () -> Unit,
    onBackClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A0520),
            Color(0xFF0D1B2A)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentUser != null) "Mi perfil"
                        else if (isRegisterMode) "Registrarse"
                        else "Iniciar sesión",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SI YA TIENE SESIÓN -> Mostrar perfil
                if (currentUser != null) {
                    Text(
                        text = "Sesión iniciada",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Mostrar nombre
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre:", color = Color(0xFFFFFFFF), fontSize = 14.sp)
                            Text(
                                currentUser.displayName ?: "Sin nombre",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar email
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Email:", color = Color(0xFFFFFFFF), fontSize = 14.sp)
                            Text(
                                currentUser.email ?: "Sin email",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón cerrar sesión
                    PrimaryClashButton(
                        text = "Cerrar sesión",
                        onClick = {
                            auth.signOut()
                            onBackClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {
                    // SI NO TIENE SESIÓN -> Mostrar login/registro
                    Text(
                        text = if (isRegisterMode) "Crea tu cuenta" else "Accede a tu cuenta",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Campo NOMBRE (solo si está en modo registro)
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it
                                errorMessage = ""},
                            label = { Text("Nombre", color = Color(0xFFFFFFFF)) },
                            singleLine = true,
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it
                            errorMessage = ""},
                        label = { Text("Email", color = Color(0xFFFFFFFF)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it
                                        errorMessage = ""},
                        label = { Text("Contraseña", color = Color(0xFFFFFFFF)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // MOSTRAR ERROR si existe
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color(0xFFFFFFFF),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón principal
                    PrimaryClashButton(
                        text = if (isLoading) "Cargando..."
                        else if (isRegisterMode) "Registrarse"
                        else "Iniciar sesión",
                        onClick = {
                            //  VALIDACIONES
                            when {
                                email.isBlank() || password.isBlank() -> {
                                    errorMessage = "Completa todos los campos"
                                    return@PrimaryClashButton
                                }
                                isRegisterMode && userName.isBlank() -> {
                                    errorMessage = "Ingresa tu nombre"
                                    return@PrimaryClashButton
                                }
                                password.length < 6 -> {
                                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                                    return@PrimaryClashButton
                                }
                            }

                            isLoading = true
                            errorMessage = ""

                            // REGISTRO
                            if (isRegisterMode) {
                                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnSuccessListener { result ->
                                        // ahora tengo que guardar el nombre
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(userName.trim())
                                            .build()

                                        result.user?.updateProfile(profileUpdates)
                                            ?.addOnSuccessListener {
                                                isLoading = false
                                                onLoginSuccess()
                                            }
                                            ?.addOnFailureListener {
                                                isLoading = false
                                                errorMessage = "Error al guardar el nombre"
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        isLoading = false
                                        errorMessage = when {
                                            exception.message?.contains("already in use") == true ->
                                                "Este email ya está registrado"
                                            exception.message?.contains("badly formatted") == true ->
                                                "Email inválido"
                                            else -> "Error al registrarse"
                                        }
                                    }
                            } else {
                                // LOGIIIIN
                                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnSuccessListener {
                                        isLoading = false
                                        onLoginSuccess()
                                    }
                                    .addOnFailureListener { exception ->
                                        isLoading = false
                                        errorMessage = when {
                                            exception.message?.contains("no user record") == true ->
                                                "Usuario no encontrado"
                                            exception.message?.contains("wrong password") == true ||
                                                    exception.message?.contains("invalid credential") == true ->
                                                "Email o contraseña incorrectos"
                                            else -> "Error al iniciar sesión"
                                        }
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle Login/Register
                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                        Text(
                            text = if (isRegisterMode) "Ya tengo cuenta" else "¿No tienes cuenta? Regístrate",
                            color = Color(0xFFFF00FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Jugar como invitado
                    TextButton(onClick = onPlayAsGuest) {
                        Text("Jugar como invitado", color = Color(0xFFFFFFFF))
                    }
                }
            }
        }
    }
}
