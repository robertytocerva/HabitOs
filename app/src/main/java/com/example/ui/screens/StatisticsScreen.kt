package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun StatisticsScreen(viewModel: MainViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val quests by viewModel.quests.collectAsState()

    val currentProfile = profile ?: return

    val completedHabitsTodayCount = habits.count { it.isCompletedToday }
    val totalCompletesAllTime = habits.sumOf { it.totalCompletions }
    val totalHabitsCount = habits.size
    val activeStreaksMax = habits.maxOfOrNull { it.streak } ?: 0
    val completedQuestsCount = quests.count { it.isCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CyberHeader(
            title = "CENTRO DE MÉTRICAS",
            subtitle = "Transmisión y rendimiento de tu hardware de vida",
            accentColor = HolographicTeal
        )

        // PERFORMANCE CARD STATS INDEX
        CyberCard(borderColor = HolographicTeal, glow = true) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ESTADÍSTICAS GLOBALES DEL SIMULADOR",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedSlate,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // LEFT COLUMN
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatBadgeItem(title = "Hábitos Completados hoy", value = "$completedHabitsTodayCount/$totalHabitsCount")
                        StatBadgeItem(title = "Total Compleciones", value = "$totalCompletesAllTime veces")
                    }
                    // RIGHT COLUMN
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatBadgeItem(title = "Racha de Hábito Máxima", value = "$activeStreaksMax días")
                        StatBadgeItem(title = "Misiones Completadas", value = "$completedQuestsCount completas")
                    }
                }
            }
        }

        // ATTRIBUTE COMBAT CHART
        CyberCard(borderColor = SpaceDeck) {
            Text(
                text = "CÁLCULADOR DE DEFENSAS COGNITIVAS",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MutedSlate,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // COMBINED FORMULAS REPRESENTING ACADEMIC STATS
            val disciplinePower = currentProfile.statDiscipline * 1.5f + currentProfile.statFocus * 1.5f
            val lifeDefenseValue = currentProfile.statEnergy * 1.0f + currentProfile.statBody * 1.5f + currentProfile.statMind * 1.2f
            
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Academic rating
                StatRatingRow(label = "Potencia Académica (DIS + FOC):", power = disciplinePower, max = 300f, color = ElectricBlue)
                // Life health rating
                StatRatingRow(label = "Resistencia al Burnout (ENE + BOD + MIN):", power = lifeDefenseValue, max = 370f, color = HolographicTeal)
            }
        }

        // INFOPANEL
        CyberCard(borderColor = SpaceDeck) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = LaserCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "INFO DE ACTUALIZACIÓN",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SoftWhite
                    )
                    Text(
                        text = "En Aura RPG, completar hábitos incrementa de inmediato tus atributos. Alcanzar el límite de XP te otorga nivel y multiplica permanentemente todos tus índices biocomputacionales.",
                        fontSize = 11.sp,
                        color = MutedSlate
                    )
                }
            }
        }

        // RESET FACTORY BUTTON
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.resetWholeGame() },
            colors = ButtonDefaults.buttonColors(containerColor = SpaceCard),
            border = BorderStroke(1.dp, NeonCrimson.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = null,
                tint = NeonCrimson
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "REINICIAR SISTEMA (HARD RESET)",
                color = NeonCrimson,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun StatBadgeItem(title: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SpaceDeck.copy(alpha = 0.5f))
            .border(1.dp, SpaceDeck, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MutedSlate,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = SoftWhite,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun StatRatingRow(label: String, power: Float, max: Float, color: Color) {
    val progress = power / max
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = SoftWhite)
            Text(text = "${power.toInt()} pt", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        CyberProgressBar(progress = progress, barColor = color, trackColor = SpaceDeck)
    }
}
