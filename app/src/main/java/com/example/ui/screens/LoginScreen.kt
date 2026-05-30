package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: com.example.viewmodel.MainViewModel,
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("aura_auth_prefs", Context.MODE_PRIVATE) }
    
    // Auth flow states
    var isSignUp by remember { mutableStateOf(false) }
    
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var errorText by remember { mutableStateOf("") }
    var successText by remember { mutableStateOf("") }

    // On switch screen, clear inputs & errors
    fun toggleMode() {
        isSignUp = !isSignUp
        email = ""
        username = ""
        password = ""
        confirmPassword = ""
        errorText = ""
        successText = ""
    }

    // Input Validations
    fun handleAuthentication() {
        errorText = ""
        successText = ""
        
        if (isSignUp) {
            // Validation rules for Sign Up
            if (email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                errorText = "TODOS LOS CAMPOS CORPORATIVOS SON OBLIGATORIOS."
                return
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorText = "FORMATO DE CORREO COGNITIVO INVÁLIDO."
                return
            }
            if (password.length < 6) {
                errorText = "LA CONTRASEÑA DEBE TENER AL MENOS 6 CARACTERES."
                return
            }
            if (password != confirmPassword) {
                errorText = "LAS CONTRASEÑAS NO CONCUERDAN EN LA CLAVE."
                return
            }
            
            viewModel.registerNewUser(
                username = username,
                email = email,
                passwordRaw = password,
                onResult = { success, msg ->
                    if (success) {
                        successText = "CUENTA ENLAZADA CORRECTAMENTE. ACCEDIENDO..."
                        onAuthenticated()
                    } else {
                        errorText = msg
                    }
                }
            )
        } else {
            // Validation rules for Log In
            if (username.isBlank() || password.isBlank()) {
                errorText = "INGRESE CREDENCIALES DE CONCIENCIA."
                return
            }
            
            viewModel.loginUser(
                identity = username,
                passwordRaw = password,
                onResult = { success, msg ->
                    if (success) {
                        successText = "ACCESO CONCEDIDO // ENLACE ESTABLECIDOS..."
                        onAuthenticated()
                    } else {
                        errorText = msg
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(SpaceCard.copy(alpha = 0.5f), DeepVoid),
                    radius = 1600f
                )
            )
            .padding(24.dp)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // CYBERPUNK AUTH LOGO
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(NeonPurple, LaserCyan)))
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(DeepVoid),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = if (errorText.isNotEmpty()) NeonCrimson else LaserCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HabitOS // RED SEGURA",
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = SoftWhite,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Text(
                text = if (isSignUp) "CREAR NUEVO EJE SENSORIAL DE OPERARIO" else "AUTENTICACIÓN VECTORIAL DE ACCESO USP",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedSlate,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // STATUS TERMINAL BOX
            CyberCard(
                borderColor = if (errorText.isNotEmpty()) NeonCrimson else if (successText.isNotEmpty()) NeonGreen else if (isSignUp) HolographicTeal else LaserCyan,
                glow = errorText.isEmpty()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUp) "SISTEMA: ASIGNANDO ALIAS OPERATIVOS" else "SISTEMA: ENLACE TERMINAL",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSignUp) HolographicTeal else LaserCyan
                    )
                    
                    if (errorText.isNotEmpty() || successText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (errorText.isNotEmpty()) errorText.uppercase() else successText.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (errorText.isNotEmpty()) NeonCrimson else NeonGreen,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TRADITIONAL LOGIN TEXTFIELDS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isSignUp) {
                    // EMAIL FIELD
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico", color = MutedSlate) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HolographicTeal) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HolographicTeal,
                            unfocusedBorderColor = SpaceDeck,
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite,
                            focusedContainerColor = SpaceCard,
                            unfocusedContainerColor = SpaceCard
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // USERNAME FIELD
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(if (isSignUp) "Nombre de Usuario (Apodo)" else "Usuario o Correo", color = MutedSlate) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LaserCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = SpaceDeck,
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedContainerColor = SpaceCard,
                        unfocusedContainerColor = SpaceCard
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // PASSWORD FIELD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña de Red", color = MutedSlate) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = SpaceDeck,
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedContainerColor = SpaceCard,
                        unfocusedContainerColor = SpaceCard
                    ),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isSignUp) {
                    // CONFIRM PASSWORD FIELD
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Contraseña", color = MutedSlate) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = SpaceDeck,
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite,
                            focusedContainerColor = SpaceCard,
                            unfocusedContainerColor = SpaceCard
                        ),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ACTION AUTH BUTTON
            CyberButton(
                text = if (isSignUp) "REGISTRAR EN RED NEURAL" else "CONECTAR OPERATIVO",
                onClick = { handleAuthentication() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TOGGLE SIGNIN OR SIGNUP
            Text(
                text = if (isSignUp) "O INICIAR SESIÓN CON CREDENCIALES EXISTENTES" else "O REGISTRAR NUEVO PERFIL DE ESTUDIANTE",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSignUp) HolographicTeal else LaserCyan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { toggleMode() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // DEVELOPER review / override banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, LaserCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .background(SpaceCard.copy(alpha = 0.4f))
                    .clickable {
                        // Quick-bypass developer credentials insertion
                        username = "admin"
                        password = "admin"
                        isSignUp = false
                        handleAuthentication()
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SettingsBackupRestore,
                    contentDescription = null,
                    tint = LaserCyan.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "BYPASS DE REVISIÓN: admin / admin",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LaserCyan.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Toca para rellenar & ingresar automáticamente.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = MutedSlate
                    )
                }
            }
        }
    }
}
