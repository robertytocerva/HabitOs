package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun HabitManagerScreen(viewModel: MainViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ACADEMIC") }
    var statAffected by remember { mutableStateOf("FOCUS") }
    var difficulty by remember { mutableStateOf("MEDIUM") }

    val categories = listOf(
        Pair("ACADEMIC", "Estudios / Académico"),
        Pair("PHYSICAL", "Entrenamiento Físico"),
        Pair("MENTAL", "Manejo de Estrés / Mental"),
        Pair("SLEEP", "Ciclo de Sueño"),
        Pair("HYDRATION", "Hidratación"),
        Pair("TIME_CONTROL", "Control de Tiempo")
    )

    val stats = listOf(
        Pair("DISCIPLINE", "Disciplina (+ Consistencia)"),
        Pair("FOCUS", "Concentración (+ Enfoque)"),
        Pair("ENERGY", "Energía (+ Resistencia)"),
        Pair("MIND", "Mente (+ Paz Emocional)"),
        Pair("BODY", "Cuerpo (+ Fuerza/Sueño)")
    )

    val difficulties = listOf("EASY", "MEDIUM", "HARD")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CyberHeader(
            title = "CODIFICADOR DE HÁBITOS",
            subtitle = "Sintoniza nuevas pautas neurales en tu interfaz",
            accentColor = LaserCyan
        )

        CyberCard(borderColor = SpaceDeck, glow = false) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = LaserCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONSOLA DE COMPILACIÓN",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftWhite
                    )
                }

                SpaceDivider()

                // TITLE
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("TÍTULO DEL HÁBITO (Ej: Leer 15 páginas)", color = MutedSlate) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = SoftWhite, fontSize = 14.sp, fontFamily = FontFamily.Monospace),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedContainerColor = SpaceDeck.copy(alpha = 0.5f),
                        unfocusedContainerColor = SpaceDeck.copy(alpha = 0.5f),
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // DESCRIPTION
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("DESCRIPCIÓN (Ej: Enfocado en optimizar mi intelecto académico)", color = MutedSlate) },
                    singleLine = false,
                    maxLines = 3,
                    textStyle = LocalTextStyle.current.copy(color = SoftWhite, fontSize = 14.sp, fontFamily = FontFamily.Monospace),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedContainerColor = SpaceDeck.copy(alpha = 0.5f),
                        unfocusedContainerColor = SpaceDeck.copy(alpha = 0.5f),
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // CATEGORY SELECTION
        Text(
            text = "ÁREA DE RED DEL HÁBITO",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedSlate
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Display first 3 horizontally
            categories.take(3).forEach { cat ->
                val isSel = category == cat.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) SpaceDeck else SpaceCard)
                        .border(1.dp, if (isSel) LaserCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { category = cat.first }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.second.split(" ").first(),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) LaserCyan else SoftWhite
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Display remaining horizontally
            categories.drop(3).forEach { cat ->
                val isSel = category == cat.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) SpaceDeck else SpaceCard)
                        .border(1.dp, if (isSel) LaserCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { category = cat.first }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.second.split(" ").first(),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) LaserCyan else SoftWhite
                    )
                }
            }
        }

        // STAT AFFECTED
        Text(
            text = "ATRIBUTO RPG BENEFICIADO",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedSlate
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            stats.forEach { stat ->
                val isSel = statAffected == stat.first
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) SpaceDeck else SpaceCard)
                        .border(1.dp, if (isSel) NeonPurple else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { statAffected = stat.first }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stat.second.uppercase(),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) NeonPurple else SoftWhite
                    )
                }
            }
        }

        // DIFFICULTY SELECTOR
        Text(
            text = "DIFICULTAD Y EXIGENCIA CUÁNTICA",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MutedSlate
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            difficulties.forEach { diff ->
                val isSel = difficulty == diff
                val diffColor = when (diff) {
                    "EASY" -> HolographicTeal
                    "MEDIUM" -> SolarAmber
                    else -> NeonCrimson
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) SpaceDeck else SpaceCard)
                        .border(1.dp, if (isSel) diffColor else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { difficulty = diff }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = diff,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (isSel) diffColor else SoftWhite
                    )
                }
            }
        }

        // SUMMARY STAT PANEL
        val xpGain = when (difficulty) {
            "EASY" -> 10
            "MEDIUM" -> 18
            else -> 30
        }
        val creditsGain = when (difficulty) {
            "EASY" -> 4
            "MEDIUM" -> 7
            else -> 12
        }
        val statGain = when (difficulty) {
            "HARD" -> 4
            "MEDIUM" -> 2
            else -> 1
        }

        CyberCard(borderColor = ElectricBlue) {
            Text(
                text = "RETORNO DE RECOPILACIÓN ESTIMADO",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Recompensa XP:", fontSize = 12.sp, color = MutedSlate)
                    Text(text = "Recompensa Créditos:", fontSize = 12.sp, color = MutedSlate)
                    Text(text = "Crecimiento de $statAffected:", fontSize = 12.sp, color = MutedSlate)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "+$xpGain XP", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = LaserCyan)
                    Text(text = "+$creditsGain CR", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = SolarAmber)
                    Text(text = "+$statGain pt", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NeonPurple)
                }
            }
        }

        // COMPILE BUTTON
        Spacer(modifier = Modifier.height(8.dp))
        CyberButton(
            text = "COMPILAR E INSTALAR HÁBITO",
            onClick = {
                viewModel.addCustomHabit(
                    title = title,
                    description = description,
                    category = category,
                    statAffected = statAffected,
                    difficulty = difficulty
                )
                // Clear state inputs
                title = ""
                description = ""
            },
            enabled = title.isNotBlank() && description.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}
