package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AICoachLog
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AICoachScreen(viewModel: MainViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val isGenerating by viewModel.isGeneratingDiagnostic.collectAsState()
    val activeReport by viewModel.activeDiagnosticReport.collectAsState()
    val coachLogs by viewModel.coachLogs.collectAsState()

    var showAiRecommenderDialog by remember { mutableStateOf(false) }
    var selectedCategoryForAi by remember { mutableStateOf("ACADEMIC") }

    // Recommendation result state
    var recommendedHabitName by remember { mutableStateOf("") }
    var recommendedHabitDesc by remember { mutableStateOf("") }
    var recommendedHabitStat by remember { mutableStateOf("FOCUS") }
    var recommendedHabitDiff by remember { mutableStateOf("MEDIUM") }
    var isRecommendationReady by remember { mutableStateOf(false) }

    val isAskingAi by viewModel.isAskingAi.collectAsState()
    val aiCoachReply by viewModel.aiCoachReply.collectAsState()
    var userQuestion by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // AI CORE NEURAL HUB SUMMARY
        item {
            CyberCard(borderColor = LaserCyan, glow = true) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Núcleo de IA",
                        tint = LaserCyan,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "NÚCLEO NEURAL GEMINI",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = SoftWhite
                    )
                    Text(
                        text = "Tu Coach cuántico de IA analiza tus hábitos y estadísticas para generar diagnósticos y recomendar metodologías personalizadas.",
                        fontSize = 12.sp,
                        color = MutedSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // DIAGNOSTIC BUTTON
                        Button(
                            onClick = { viewModel.runNeuralDiagnostic() },
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                            border = BorderStroke(1.dp, LaserCyan),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SettingsSuggest,
                                contentDescription = null,
                                tint = LaserCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ESCANEAR CORE",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SoftWhite
                            )
                        }

                        // ASK RECOMMANDATION
                        Button(
                            onClick = {
                                recommendedHabitName = ""
                                recommendedHabitDesc = ""
                                isRecommendationReady = false
                                showAiRecommenderDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                            border = BorderStroke(1.dp, NeonPurple),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sugerir Hábito",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SoftWhite
                            )
                        }
                    }
                }
            }
        }

        // LOADING OR ACTIVE REPORT SUMMARY
        if (isGenerating || activeReport != null) {
            item {
                CyberCard(borderColor = LaserCyan) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Article,
                                contentDescription = null,
                                tint = LaserCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REPORTE NEURAL ACTIVO",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SoftWhite
                            )
                        }
                        
                        SpaceDivider(modifier = Modifier.padding(vertical = 12.dp))

                        if (isGenerating && activeReport?.startsWith("Calculando") == true) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = LaserCyan,
                                trackColor = SpaceDeck
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Text(
                            text = activeReport ?: "",
                            fontSize = 13.sp,
                            color = SoftWhite,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // ASK AI DIRECT QUESTIONS SECTION
        item {
            CyberCard(borderColor = NeonPurple, glow = true) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "CONSULTA LIBRE AL NÚCLEO IA",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SoftWhite
                            )
                            Text(
                                text = "Comunica tus dudas existenciales o académicas al Coach.",
                                fontSize = 11.sp,
                                color = MutedSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // TextField Input
                    OutlinedTextField(
                        value = userQuestion,
                        onValueChange = { userQuestion = it },
                        placeholder = { Text("Ej: ¿Cómo evito procrastinar estudiando cálculo?", color = MutedSlate.copy(alpha = 0.6f), fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = SpaceDeck,
                            focusedContainerColor = SpaceCard,
                            unfocusedContainerColor = SpaceCard
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Suggestion Chip Pills
                    Text(
                        text = "SUGERENCIAS DE ENLACE DE RED (TOQUE RÁPIDO):",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedSlate,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val suggestions = listOf(
                            "Evitar procrastinar",
                            "Dormir en exámenes",
                            "Enfoque en 2 mins"
                        )
                        suggestions.forEach { sug ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpaceCard)
                                    .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        userQuestion = when (sug) {
                                            "Evitar procrastinar" -> "¿Qué estrategia rápida de 5 minutos me recomiendas para dejar de procrastinar ahora mismo?"
                                            "Dormir en exámenes" -> "Tengo mucha ansiedad por exámenes y no puedo descansar bien, dame un protocolo tecnológico de sueño."
                                            else -> "Necesito sintonizar mi enfoque en menos de 2 minutos. ¿Qué ritual mental rápido de alto rendimiento me sugieres?"
                                        }
                                        viewModel.askAiFree(userQuestion)
                                        userQuestion = "" // Clear field for answers
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sug,
                                    fontSize = 10.sp,
                                    color = LaserCyan,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Execute query buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = "PROPAGAR PREGUNTA",
                            onClick = {
                                if (userQuestion.isNotBlank()) {
                                    viewModel.askAiFree(userQuestion)
                                    userQuestion = "" // clear input box
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        if (aiCoachReply != null) {
                            Button(
                                onClick = { viewModel.clearAiCoachReply() },
                                colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                                border = BorderStroke(0.5.dp, NeonCrimson),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("LIMPIAR", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = NeonCrimson)
                            }
                        }
                    }

                    // DISPLAY SCREEN FOR RESPONSE WITH GLOW
                    if (isAskingAi || aiCoachReply != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SpaceDeck)
                                .border(1.dp, LaserCyan, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isAskingAi) NeonPurple else NeonGreen)
                                    )
                                    Text(
                                        text = if (isAskingAi) "CONEXIÓN ESTABLECIDA // ANALIZANDO DUDA..." else "CONEXIÓN HISTÓRICA // ENLACE COMPLETO",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAskingAi) NeonPurple else NeonGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = aiCoachReply ?: "",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = SoftWhite,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // HISTORICAL LOGS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberHeader(
                    title = "HISTÓRICOS COGNITIVOS",
                    subtitle = "Registro cuántico de tus diagnósticos pasados",
                    accentColor = NeonPurple
                )
                if (coachLogs.isNotEmpty()) {
                    Text(
                        text = "LIMPIAR",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCrimson,
                        modifier = Modifier
                            .clickable { viewModel.clearDiagnosticLogs() }
                            .padding(8.dp)
                    )
                }
            }
        }

        if (coachLogs.isEmpty()) {
            item {
                CyberCard(borderColor = MutedSlate.copy(alpha = 0.2f)) {
                    Text(
                        text = "No hay diagnósticos holográficos previos guardados. Presiona 'ESCANEAR CORE' para iniciar de inmediato.",
                        fontSize = 12.sp,
                        color = MutedSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }
        } else {
            items(coachLogs, key = { it.id }) { log ->
                HistoryLogCard(log = log)
            }
        }
    }

    // --- AI RECOMMENDATION DIALOG ---
    if (showAiRecommenderDialog) {
        Dialog(onDismissRequest = { showAiRecommenderDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DeepVoid)
                    .border(1.dp, NeonPurple, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SUGERIDOR CUÁNTICO IA",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = NeonPurple,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Genera un hábito de alta tecnología científica mediante un diagnóstico rápido con Gemini.",
                        fontSize = 11.sp,
                        color = MutedSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (!isRecommendationReady) {
                        Text(
                            text = "SELECCIONA CATEGORÍA DE ENFOQUE:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = SoftWhite,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        val categories = listOf(
                            CategorySelection("ACADEMIC", "Estudio / Académico", ElectricBlue),
                            CategorySelection("SLEEP", "Sueño y Descanso", LaserCyan),
                            CategorySelection("PHYSICAL", "Físico / Deporte", NeonGreen),
                            CategorySelection("MENTAL", "Mental / Anti-Estrés", NeonPurple),
                            CategorySelection("HYDRATION", "Hidratación", HolographicTeal)
                        )

                        categories.forEach { cat ->
                            val isSel = selectedCategoryForAi == cat.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) SpaceDeck else SpaceCard)
                                    .border(1.dp, if (isSel) cat.color else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { selectedCategoryForAi = cat.id }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = cat.label.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) cat.color else SoftWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isGenerating) {
                            CircularProgressIndicator(color = NeonPurple)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Sintonizando Gemini...", color = MutedSlate, fontSize = 11.sp)
                        } else {
                            CyberButton(
                                text = "CONSULTAR IA",
                                onClick = {
                                    viewModel.recommendNewHabitWithAi(selectedCategoryForAi) { name, desc, stat, diff ->
                                        recommendedHabitName = name
                                        recommendedHabitDesc = desc
                                        recommendedHabitStat = stat
                                        recommendedHabitDiff = diff
                                        isRecommendationReady = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        // RECOMMENDATION PREVIEW AND APPROVAL
                        CyberCard(borderColor = LaserCyan) {
                            Text(
                                text = "¡SUGERENCIA ENCONTRADA!",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = LaserCyan,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = recommendedHabitName.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SoftWhite
                            )
                            Text(
                                text = recommendedHabitDesc,
                                fontSize = 12.sp,
                                color = MutedSlate,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MEJORA: ${recommendedHabitStat}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPurple
                                )
                                Text(
                                    text = "DIFICULTAD: ${recommendedHabitDiff}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SolarAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { isRecommendationReady = false },
                                colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "REINTENTAR", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SoftWhite)
                            }
                            Button(
                                onClick = {
                                    viewModel.addCustomHabit(
                                        title = recommendedHabitName,
                                        description = recommendedHabitDesc,
                                        category = selectedCategoryForAi,
                                        statAffected = recommendedHabitStat,
                                        difficulty = recommendedHabitDiff
                                    )
                                    showAiRecommenderDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text(text = "INSTALAR HÁBITO", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SoftWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryLogCard(log: AICoachLog) {
    val date = Date(log.timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(date)

    CyberCard(borderColor = SpaceDeck) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = log.notificationTitle,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = LaserCyan
            )
            Text(
                text = formattedDate,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = MutedSlate
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = log.aiMessage,
            fontSize = 11.sp,
            color = MutedSlate,
            fontFamily = FontFamily.Monospace,
            maxLines = 4,
            lineHeight = 15.sp
        )
    }
}

data class CategorySelection(
    val id: String,
    val label: String,
    val color: Color
)
