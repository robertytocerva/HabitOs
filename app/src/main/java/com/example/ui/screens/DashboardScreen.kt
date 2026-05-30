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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.Quest
import com.example.data.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

fun getActiveAccentColor(profile: UserProfile): Color {
    return when (profile.purchasedTheme) {
        "Verde Esmeralda" -> Color(0xFF10B981)
        "Fuego Solar" -> Color(0xFFEF4444)
        "Azul Cósmico" -> Color(0xFF3B82F6)
        else -> {
            when (profile.characterClass) {
                "Codex-Scribe" -> ElectricBlue
                "Cyber-Runner" -> LaserCyan
                "Mind-Sentry" -> NeonPurple
                "Nano-Titan" -> HolographicTeal
                else -> LaserCyan
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    val profile by viewModel.userProfile.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val quests by viewModel.quests.collectAsState()

    val currentProfile = profile ?: return
    val activeAccentColor = getActiveAccentColor(currentProfile)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp) // Avoid overlap with bottom nav
    ) {
        // PLAYER HERO HUD CARD
        item {
            HeroHudCard(profile = currentProfile, themeColor = activeAccentColor, onLogoutClick = onLogout)
        }

        // MARKET COGNITIVE SHOP BUTTON (REDEEM EARNED COINS)
        item {
            var showShop by remember { mutableStateOf(false) }
            
            CyberCard(borderColor = SolarAmber) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showShop = true }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Mercado Quántico",
                            tint = SolarAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MERCADO DE RED / CANJE DE CRÉDITOS",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SoftWhite
                            )
                            Text(
                                text = "Gasta tu saldo de [ ${currentProfile.credits} CR ] en inyecciones de XP, títulos de rol o temas.",
                                fontSize = 10.sp,
                                color = MutedSlate
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SolarAmber
                    )
                }
            }

            if (showShop) {
                AlertDialog(
                    onDismissRequest = { showShop = false },
                    confirmButton = {
                        TextButton(onClick = { showShop = false }) {
                            Text("SALIR DE MERCADO", fontFamily = FontFamily.Monospace, color = LaserCyan)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = SolarAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "MERCADO CRIPTO-SEC",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SoftWhite,
                                fontSize = 15.sp
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SpaceDeck, RoundedCornerShape(8.dp))
                                    .border(1.dp, SolarAmber, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Paid, contentDescription = null, tint = SolarAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "TUS CRÉDITOS: ${currentProfile.credits} CR",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = SolarAmber,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            // 1. EQUIPABLE CODENAME TITLES
                            Text(
                                "TÍTULOS CRIPTOGRÁFICOS",
                                fontFamily = FontFamily.Monospace,
                                color = LaserCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val titles = listOf(
                                "MASTER HACKER" to 25,
                                "INTELECTO MAX" to 40,
                                "SOBERANO SUEÑO" to 55,
                                "SINTAXIS MAESTRA" to 75
                            )

                            titles.forEach { (title, cost) ->
                                val isOwned = currentProfile.purchasedTitle == title
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SpaceCard, RoundedCornerShape(8.dp))
                                        .border(
                                            width = 1.dp,
                                            color = if (isOwned) LaserCyan else Color(0x13FFFFFF),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(title, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        Text("Badge visible en perfil", color = MutedSlate, fontSize = 10.sp)
                                    }
                                    if (isOwned) {
                                        Text("[ACTIVO]", color = LaserCyan, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Button(
                                            onClick = { viewModel.buyTitle(title, cost) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                                            border = BorderStroke(1.dp, SolarAmber),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("$cost CR", color = SolarAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // 2. XP SPEED BOOSTS
                            Text(
                                "INYECCIONES DE XP (NIVEL RÁPIDO)",
                                fontFamily = FontFamily.Monospace,
                                color = HolographicTeal,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val injectors = listOf(
                                "Inyector Cripto (+40 XP)" to (40 to 30),
                                "Supercarga Neuronal (+100 XP)" to (100 to 60)
                            )

                            injectors.forEach { (name, pair) ->
                                val (xp, cost) = pair
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SpaceCard, RoundedCornerShape(8.dp))
                                        .border(width = 1.dp, color = Color(0x13FFFFFF), shape = RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        Text("Sube de nivel instantáneamente", color = MutedSlate, fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.purchaseXpInjection(xp, cost) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                                        border = BorderStroke(1.dp, SolarAmber),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("$cost CR", color = SolarAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // 2.5 POTENCIADORES CYBER-SENSORIALES
                            Text(
                                "MODULADORES DE ATRIBUTOS (+5 UNIDADES)",
                                fontFamily = FontFamily.Monospace,
                                color = SolarAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val statBoosters = listOf(
                                Triple("Amplificador de Disciplina", "DISCIPLINE", 35),
                                Triple("Inyector de Enfoque/Focus", "FOCUS", 35),
                                Triple("Celda de Energía Cósmica", "ENERGY", 35),
                                Triple("Neuro-Sintonizador Mental", "MIND", 35),
                                Triple("Estimulante Biológico (Físico)", "BODY", 35)
                            )

                            statBoosters.forEach { (name, stat, cost) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SpaceCard, RoundedCornerShape(8.dp))
                                        .border(width = 1.dp, color = Color(0x13FFFFFF), shape = RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        Text("Aumenta $stat permanentemente en +5", color = MutedSlate, fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.purchaseStatBoost(stat, cost) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                                        border = BorderStroke(1.dp, SolarAmber),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("$cost CR", color = SolarAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // 3. COLOR SKINS FOR HUD
                            Text(
                                "ASPECTOS DE COLOR DE INTERFAZ",
                                fontFamily = FontFamily.Monospace,
                                color = NeonPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val skins = listOf(
                                "Verde Esmeralda" to 30,
                                "Fuego Solar" to 50,
                                "Azul Cósmico" to 70,
                                "Estándar clase (Restaurar)" to 0
                            )

                            skins.forEach { (colorName, cost) ->
                                val activeTheme = if (colorName == "Estándar clase (Restaurar)") "" else colorName
                                val isActive = currentProfile.purchasedTheme == activeTheme
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SpaceCard, RoundedCornerShape(8.dp))
                                        .border(
                                            width = 1.dp,
                                            color = if (isActive) NeonPurple else Color(0x13FFFFFF),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(colorName, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        Text("Modula los colores del emulador", color = MutedSlate, fontSize = 10.sp)
                                    }
                                    if (isActive) {
                                        Text("[ACTIVO]", color = NeonPurple, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Button(
                                            onClick = { viewModel.buyTheme(activeTheme, cost) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck),
                                            border = BorderStroke(1.dp, SolarAmber),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(if (cost == 0) "GRATIS" else "$cost CR", color = SolarAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    containerColor = DeepVoid,
                    textContentColor = SoftWhite,
                    titleContentColor = SoftWhite,
                    modifier = Modifier.border(1.dp, SolarAmber, RoundedCornerShape(24.dp))
                )
            }
        }

        // ATTRIBUTE RATIOS SCREEN
        item {
            AttributesCard(profile = currentProfile)
        }

        // CORE HABITS CHECKLIST
        item {
            CyberHeader(
                title = "Hábitos Activos",
                subtitle = "Sintoniza tus rutinas diarias para ganar XP",
                accentColor = activeAccentColor
            )
        }

        if (habits.isEmpty()) {
            item {
                CyberCard(borderColor = MutedSlate.copy(alpha = 0.3f)) {
                    Text(
                        text = "Red de hábitos vacía. Ve a la pestaña de 'Codificar' para registrar nuevos hábitos o deja que la IA recomiende algunos.",
                        fontSize = 13.sp,
                        color = MutedSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                HabitRowItem(
                    habit = habit,
                    onCheckClick = { viewModel.completeHabit(habit.id) },
                    onDeleteClick = { viewModel.deleteHabit(habit.id) }
                )
            }
        }

        // DAILY QUESTS MODULE
        item {
            CyberHeader(
                title = "Misiones de Red",
                subtitle = "Tareas limitadas con recompensas extras",
                accentColor = SolarAmber
            )
        }

        if (quests.isEmpty()) {
            item {
                CyberCard(borderColor = MutedSlate.copy(alpha = 0.2f)) {
                    Text(
                        text = "No hay misiones activas por hoy. ¡Felicidades! Consola despejada.",
                        fontSize = 13.sp,
                        color = MutedSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }
        } else {
            items(quests, key = { it.id }) { quest ->
                QuestItemCard(
                    quest = quest,
                    onCompleteClick = { viewModel.completeQuest(quest.id) }
                )
            }
        }
    }
}

@Composable
fun HeroHudCard(profile: UserProfile, themeColor: Color, onLogoutClick: (() -> Unit)? = null) {
    val xpProgress = profile.xp.toFloat() / profile.xpToNextLevel.toFloat()

    val classIcon = when (profile.characterClass) {
        "Codex-Scribe" -> Icons.Default.MenuBook
        "Cyber-Runner" -> Icons.Default.FlashOn
        "Mind-Sentry" -> Icons.Default.SelfImprovement
        "Nano-Titan" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Person
    }

    CyberCard(borderColor = themeColor, glow = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CLASS AVATAR BADGE WITH DOUBLE-BORDER & LEVEL OVERLAY
            Box {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(2.dp, themeColor, CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(themeColor, ElectricBlue)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = classIcon,
                        contentDescription = profile.characterClass,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Absolute Badge in corners
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(themeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LVL ${profile.level}",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // STATS DETAILS
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = profile.name.uppercase(),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = SoftWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (profile.purchasedTitle.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(SolarAmber.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .border(1.dp, SolarAmber, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = profile.purchasedTitle,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SolarAmber
                                )
                            }
                        }
                    }
                    if (onLogoutClick != null) {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = NeonCrimson
                            )
                        }
                    }
                }
                Text(
                    text = profile.getClassTitleSpanish(),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = themeColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                // CREDITS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "Créditos",
                        tint = SolarAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.credits} CRÉDITOS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MutedSlate
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // XP HUD
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CARGA COGNITIVA XP",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MutedSlate,
                letterSpacing = 1.sp
            )
            Text(
                text = "${profile.xp} / ${profile.xpToNextLevel} XP",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = themeColor,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        CyberProgressBar(progress = xpProgress, barColor = themeColor)
    }
}

@Composable
fun AttributesCard(profile: UserProfile) {
    CyberCard(borderColor = NeonPurple.copy(alpha = 0.3f)) {
        Text(
            text = "MATRIZ DE ATRIBUTOS COGNITIVOS",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MutedSlate,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val stats = listOf(
            AttributeItem("DISCIPLINA", profile.statDiscipline, "Asistencia y constancia general", ElectricBlue),
            AttributeItem("CONCENTRACIÓN (FOCUS)", profile.statFocus, "Bloques de estudio profundo", LaserCyan),
            AttributeItem("ENERGÍA (ESTAMINA)", profile.statEnergy, "Hidratación y vitalidad física", SolarAmber),
            AttributeItem("SALUD MENTAL (MIND)", profile.statMind, "Resistencia emocional y meditación", NeonPurple),
            AttributeItem("SALUD FÍSICA (BODY)", profile.statBody, "Calidad del sueño y entrenamiento", HolographicTeal)
        )

        stats.forEach { stat ->
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stat.label,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite
                        )
                        Text(
                            text = stat.subtitle,
                            fontSize = 9.sp,
                            color = MutedSlate
                        )
                    }
                    Text(
                        text = "${stat.value}/100",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = stat.color
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                CyberProgressBar(
                    progress = stat.value / 100f,
                    barColor = stat.color,
                    trackColor = SpaceDeck.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun HabitRowItem(
    habit: Habit,
    onCheckClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val catColor = Color(android.graphics.Color.parseColor(habit.getCategoryColorHex()))

    val backgroundBrush = if (habit.isCompletedToday) {
        Brush.horizontalGradient(
            colors = listOf(catColor.copy(alpha = 0.15f), Color.Transparent)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0x0DFFFFFF), Color(0x03FFFFFF))
        )
    }

    val borderBrush = Brush.horizontalGradient(
        colors = listOf(
            if (habit.isCompletedToday) catColor.copy(alpha = 0.5f) else Color(0x13FFFFFF),
            Color(0x05FFFFFF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundBrush)
            .border(
                width = 0.8.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .drawBehind {
                // Draw a sleek left bar indicator for completed ones
                if (habit.isCompletedToday) {
                    drawRect(
                        color = catColor,
                        size = androidx.compose.ui.geometry.Size(width = 3.dp.toPx(), height = size.height)
                    )
                }
            }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // COMPLETED CHECK STATE BUTTON (Touch target size >= 48dp)
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (habit.isCompletedToday) catColor.copy(alpha = 0.2f) else SpaceDeck)
                    .clickable { onCheckClick() }
                    .border(1.dp, if (habit.isCompletedToday) catColor else MutedSlate.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (habit.isCompletedToday) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completado",
                        tint = catColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MutedSlate.copy(alpha = 0.5f))
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // HABIT BODY
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(catColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = habit.getCategorySpanish().uppercase(),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = catColor
                        )
                    }
                    
                    if (habit.streak > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = SolarAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${habit.streak}D",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SolarAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = habit.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (habit.isCompletedToday) SoftWhite.copy(alpha = 0.6f) else SoftWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = habit.description,
                    fontSize = 11.sp,
                    color = MutedSlate,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // REWARDS ACCENT + TRASH
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "+${habit.xpReward} XP",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LaserCyan
                )
                Text(
                    text = "+${habit.creditsReward} CR",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = SolarAmber
                )
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar hábito",
                    tint = NeonCrimson.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDeleteClick() }
                )
            }
        }
    }
}

@Composable
fun QuestItemCard(
    quest: Quest,
    onCompleteClick: () -> Unit
) {
    val questColor = when (quest.rank) {
        "S-RANK" -> NeonCrimson
        "A-RANK" -> NeonPurple
        "B-RANK" -> SolarAmber
        "C-RANK" -> ElectricBlue
        else -> HolographicTeal
    }

    val backgroundBrush = if (quest.isCompleted) {
        Brush.horizontalGradient(
            colors = listOf(Color(0x06FFFFFF), Color(0x02FFFFFF))
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(questColor.copy(alpha = 0.1f), Color(0x04FFFFFF))
        )
    }

    val borderBrush = Brush.horizontalGradient(
        colors = listOf(
            if (quest.isCompleted) Color(0x13FFFFFF) else questColor.copy(alpha = 0.45f),
            Color(0x05FFFFFF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundBrush)
            .border(
                width = 0.8.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // RANK RADAR ICON
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (quest.isCompleted) SpaceDeck.copy(alpha = 0.3f) else questColor.copy(alpha = 0.12f))
                    .border(1.dp, if (quest.isCompleted) MutedSlate.copy(alpha = 0.3f) else questColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quest.rank.take(1),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = if (quest.isCompleted) MutedSlate else questColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // BODY
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title.uppercase(),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (quest.isCompleted) MutedSlate else SoftWhite
                )
                Text(
                    text = quest.description,
                    fontSize = 11.sp,
                    color = MutedSlate,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // COMPLETE BUTTON
            Spacer(modifier = Modifier.width(8.dp))
            if (quest.isCompleted) {
                Text(
                    text = "COMPLETED",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                )
            } else {
                Button(
                    onClick = onCompleteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceDeck.copy(alpha = 0.6f)),
                    border = BorderStroke(0.5.dp, questColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "RECLAMAR",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = questColor
                    )
                }
            }
        }
    }
}

data class AttributeItem(
    val label: String,
    val value: Int,
    val subtitle: String,
    val color: Color
)
