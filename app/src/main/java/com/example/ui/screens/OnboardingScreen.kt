package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 4

    val nickname by viewModel.onboardingNickname.collectAsState()
    val characterClass by viewModel.onboardingClass.collectAsState()
    val stress by viewModel.onboardingStress.collectAsState()
    val sleep by viewModel.onboardingSleep.collectAsState()
    val energy by viewModel.onboardingEnergy.collectAsState()
    val procrastination by viewModel.onboardingProcrastination.collectAsState()
    val workhours by viewModel.onboardingWorkhours.collectAsState()
    val goal by viewModel.onboardingGoal.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(SpaceDeck.copy(alpha = 0.4f), DeepVoid),
                    radius = 1600f
                )
            )
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // STEP PROGRESS HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HabitOS // DIAGNÓSTICO V2.0",
                    fontFamily = FontFamily.Monospace,
                    color = LaserCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "CALIBRACIÓN $currentStep / $totalSteps",
                    fontFamily = FontFamily.Monospace,
                    color = NeonPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            
            CyberProgressBar(
                progress = currentStep.toFloat() / totalSteps.toFloat(),
                barColor = NeonPurple,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // CONTENT STEP CONTAINER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStep) {
                    1 -> Step1Identity(nickname = nickname, onNicknameSelected = { viewModel.onboardingNickname.value = it })
                    2 -> Step2Diagnostics(
                        stress = stress,
                        onStressChange = { viewModel.onboardingStress.value = it },
                        sleep = sleep,
                        onSleepChange = { viewModel.onboardingSleep.value = it },
                        energy = energy,
                        onEnergyChange = { viewModel.onboardingEnergy.value = it },
                        procrastination = procrastination,
                        onProcrastinationChange = { viewModel.onboardingProcrastination.value = it }
                    )
                    3 -> Step3Lifestyle(
                        workhours = workhours,
                        onWorkhoursChange = { viewModel.onboardingWorkhours.value = it },
                        goal = goal,
                        onGoalChange = { viewModel.onboardingGoal.value = it }
                    )
                    4 -> Step4ClassSelection(
                        selectedClass = characterClass,
                        onClassSelected = { viewModel.onboardingClass.value = it }
                    )
                }
            }

            // NAVIGATION BUTTONS Row
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    IconButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .size(52.dp)
                            .border(1.dp, LaserCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .background(SpaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = LaserCyan
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(52.dp))
                }

                if (currentStep < totalSteps) {
                    IconButton(
                        onClick = {
                            if (currentStep == 1 && nickname.isBlank()) {
                                // Do nothing, nickname is required
                            } else {
                                currentStep++
                            }
                        },
                        enabled = currentStep > 1 || nickname.isNotBlank(),
                        modifier = Modifier
                            .size(52.dp)
                            .border(
                                width = 1.dp,
                                color = if (nickname.isNotBlank()) LaserCyan else MutedSlate.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(if (nickname.isNotBlank()) SpaceDeck else SpaceCard.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Siguiente",
                            tint = if (nickname.isNotBlank()) LaserCyan else MutedSlate
                        )
                    }
                } else {
                    CyberButton(
                        text = "ENLAZAR COGNICIÓN",
                        onClick = { viewModel.completeOnboarding() },
                        modifier = Modifier.weight(1f).padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Step1Identity(nickname: String, onNicknameSelected: (String) -> Unit) {
    val presets = listOf(
        "Runner-X9", "Aero-Codex", "Aegis-One", "Neo-Scribe", 
        "Sentry-V", "Cyber-Vortex", "Sintaxis", "Zero-Titan", "Spectre-9"
    )

    CyberCard(borderColor = LaserCyan, glow = true) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = LaserCyan,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "REGISTRO DE CONCIENCIA",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = SoftWhite
            )
            Text(
                text = "Seleccione su alias de red y código operativo. No requiere entrada de teclado.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MutedSlate,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Selection Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpaceDeck)
                    .border(1.dp, LaserCyan, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (nickname.isBlank()) "SELECCIONE CLAVE" else "APODO: $nickname",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (nickname.isBlank()) MutedSlate else LaserCyan
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Code presets Grid
            Text(
                text = "OPERATIVOS DISPONIBLES EN LA RED:",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = MutedSlate,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // Dynamic grid layout without heavy wrappers
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = presets.chunked(3)
                chunks.forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunk.forEach { title ->
                            val isSelected = nickname == title
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) LaserCyan.copy(alpha = 0.2f) else SpaceCard)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) LaserCyan else Color(0x13FFFFFF),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onNicknameSelected(title) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) LaserCyan else SoftWhite
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick random generation button
            Button(
                onClick = {
                    val randomPrefixes = listOf("Hype", "Voxel", "Hex", "Nexus", "Cypher", "Quant", "Scribe", "Besta")
                    val randomSuffixes = listOf("Alpha", "Omega", "Prime", "Zero", "Nova", "Flux", "Sigma", "99")
                    val generated = "${randomPrefixes.random()}-${randomSuffixes.random()}"
                    onNicknameSelected(generated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SpaceCard),
                border = BorderStroke(1.dp, HolographicTeal.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    tint = HolographicTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GENERAR APODO ALEATORIO",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = HolographicTeal
                )
            }
        }
    }
}

@Composable
fun Step2Diagnostics(
    stress: Int,
    onStressChange: (Int) -> Unit,
    sleep: Int,
    onSleepChange: (Int) -> Unit,
    energy: Int,
    onEnergyChange: (Int) -> Unit,
    procrastination: Int,
    onProcrastinationChange: (Int) -> Unit
) {
    CyberCard(borderColor = NeonPurple) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DIAGNÓSTICO PSICOSOMÁTICO",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SoftWhite
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Análisis biomecánico profundo. Calibrando medidores de resistencia somática.",
                fontSize = 12.sp,
                color = MutedSlate,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // STRESS INDEX
            Text(
                text = "1. ESTRÉS UNIVERSITARIO: $stress / 10",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = LaserCyan
            )
            Slider(
                value = stress.toFloat(),
                onValueChange = { onStressChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = LaserCyan,
                    activeTrackColor = LaserCyan,
                    inactiveTrackColor = SpaceDeck
                )
            )
            Text(
                text = when {
                    stress <= 3 -> "Presión normal (Zen y equilibrado)"
                    stress <= 7 -> "Estrés Académico Elevado (Sobrecargado)"
                    else -> "¡Sobrecarga Crítica! Alarma de Burnout"
                },
                fontSize = 10.sp,
                color = MutedSlate,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // SLEEP INDEX
            Text(
                text = "2. CALIDAD DE SUEÑO / DESCANSO: $sleep / 10",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = HolographicTeal
            )
            Slider(
                value = sleep.toFloat(),
                onValueChange = { onSleepChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = HolographicTeal,
                    activeTrackColor = HolographicTeal,
                    inactiveTrackColor = SpaceDeck
                )
            )
            Text(
                text = when {
                    sleep <= 3 -> "Ciclo destruido (Madrugadas de exámenes)"
                    sleep <= 7 -> "Sistemas estables (Dormir regular)"
                    else -> "Modo Regeneración Óptimo (Descanso excelente)"
                },
                fontSize = 10.sp,
                color = MutedSlate,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // COGNITIVE ENERGY INDEX
            Text(
                text = "3. RESERVA DE ENERGÍA COGNITIVA: $energy / 10",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = SolarAmber
            )
            Slider(
                value = energy.toFloat(),
                onValueChange = { onEnergyChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = SolarAmber,
                    activeTrackColor = SolarAmber,
                    inactiveTrackColor = SpaceDeck
                )
            )
            Text(
                text = when {
                    energy <= 3 -> "Agotamiento severo (En reserva)"
                    energy <= 7 -> "Energía moderada / Estable"
                    else -> "Hiper-enfoque cuántico activado"
                },
                fontSize = 10.sp,
                color = MutedSlate,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // PROCRASTINATION INDEX
            Text(
                text = "4. RIESGO DE PROCRASTINACIÓN: $procrastination / 10",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = NeonCrimson
            )
            Slider(
                value = procrastination.toFloat(),
                onValueChange = { onProcrastinationChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = NeonCrimson,
                    activeTrackColor = NeonCrimson,
                    inactiveTrackColor = SpaceDeck
                )
            )
            Text(
                text = when {
                    procrastination <= 3 -> "Alta autodisciplina operativa"
                    procrastination <= 7 -> "Postergación moderada por distracción"
                    else -> "Procrastinación Crítica. Bloqueo obligatorio"
                },
                fontSize = 10.sp,
                color = MutedSlate
            )
        }
    }
}

@Composable
fun Step3Lifestyle(
    workhours: String,
    onWorkhoursChange: (String) -> Unit,
    goal: String,
    onGoalChange: (String) -> Unit
) {
    val blockOptions = listOf(
        "Mañana" to "Bloque de Mañana (08:00 - 12:00)",
        "Tarde" to "Bloque de Tarde (13:00 - 17:00)",
        "Noche" to "Bloque de la Noche (18:00 - 22:00)",
        "Mixto" to "Bloque Mixto / Flexible"
    )

    val goalOptions = listOf(
        "Tesis" to "Enfoque de Alto Impacto (Proyectos mayor escala, tesis o investigación)",
        "Materias" to "Progreso de Resistencia (Salvar asignaturas complejas y alta disciplina)",
        "Salud" to "Balance y Estilo Sano (Enfoque en vitalidad corporal, descanso e hidratación)",
        "Procrastinacion" to "Modo Anti-Postergador (Dominar el tiempo, hackear la consistencia)"
    )

    CyberCard(borderColor = HolographicTeal) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = HolographicTeal,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DINÁMICA DE OPERACIÓN",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SoftWhite
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Modula tus bloques activos de estudio y tu dinámica operativa de progreso inmediato.",
                fontSize = 12.sp,
                color = MutedSlate,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Study blocks
            Text(
                text = "SELECCIONE BLOQUE DE ESTUDIO:",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = HolographicTeal,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                blockOptions.forEach { (id, label) ->
                    val isSel = workhours == id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) HolographicTeal.copy(alpha = 0.15f) else SpaceCard)
                            .border(width = 1.dp, color = if (isSel) HolographicTeal else Color(0x13FFFFFF), shape = RoundedCornerShape(8.dp))
                            .clickable { onWorkhoursChange(id) }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSel,
                                onClick = { onWorkhoursChange(id) },
                                colors = RadioButtonDefaults.colors(selectedColor = HolographicTeal, unselectedColor = MutedSlate)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, color = SoftWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Goal selector
            Text(
                text = "MODALIDAD DE RITMO OPERATIVO (DINÁMICO):",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = HolographicTeal,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                goalOptions.forEach { (id, label) ->
                    val isSel = goal == id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) HolographicTeal.copy(alpha = 0.15f) else SpaceCard)
                            .border(width = 1.dp, color = if (isSel) HolographicTeal else Color(0x13FFFFFF), shape = RoundedCornerShape(8.dp))
                            .clickable { onGoalChange(id) }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSel,
                                onClick = { onGoalChange(id) },
                                colors = RadioButtonDefaults.colors(selectedColor = HolographicTeal, unselectedColor = MutedSlate)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, color = SoftWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step4ClassSelection(
    selectedClass: String,
    onClassSelected: (String) -> Unit
) {
    val classes = listOf(
        ClassesItem("Cyber-Runner", "Cyber-Runner (Velocidad / Disciplina)", "Hackea la procrastinación optimizando calendarios y descansos.", Icons.Default.FlashOn, LaserCyan),
        ClassesItem("Codex-Scribe", "Escriba del Códice (Enfoque Académico)", "Concentración científica para estudiar de forma profunda.", Icons.Default.MenuBook, ElectricBlue),
        ClassesItem("Mind-Sentry", "Baluarte Mental (Manejo de Estrés)", "Medita y fortalece tus sistemas neuronales frente a presiones.", Icons.Default.SelfImprovement, NeonPurple),
        ClassesItem("Nano-Titan", "Nano-Titán (Físico / Cuerpo)", "Convierte el ejercicio físico en energía de alto calibre cerebral.", Icons.Default.FitnessCenter, HolographicTeal)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SELECCIONA TU CLASE DE HÉROE",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = SoftWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        classes.forEach { item ->
            val isSelected = selectedClass == item.id
            val borderCol = if (isSelected) item.color else SpaceDeck.copy(alpha = 0.3f)
            val bgCol = if (isSelected) SpaceDeck.copy(alpha = 0.6f) else SpaceCard.copy(alpha = 0.5f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgCol)
                    .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                    .clickable { onClassSelected(item.id) }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = item.color
                        )
                        Text(
                            text = item.desc,
                            fontSize = 11.sp,
                            color = MutedSlate,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Seleccionado",
                            tint = item.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

data class ClassesItem(
    val id: String,
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
