package com.example

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.AppDatabase
import com.example.data.repository.AppRepository
import com.example.data.repository.LevelUpResult
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core room persistence instantiations via simple injection
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(
            userDao = database.userDao(),
            userCredentialDao = database.userCredentialDao(),
            habitDao = database.habitDao(),
            questDao = database.questDao(),
            aiCoachLogDao = database.aiCoachLogDao()
        )
        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application, repository)
        )[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val context = LocalContext.current
    val profileState by viewModel.userProfile.collectAsStateWithLifecycle()
    
    val sharedPrefs = remember { context.getSharedPreferences("aura_habit_rpg_prefs", Context.MODE_PRIVATE) }
    val savedUsername = remember { sharedPrefs.getString("logged_in_username", "") ?: "" }
    val savedUserId = remember { sharedPrefs.getInt("logged_in_userid", -1) }

    // Auth gate state
    var isAuthenticated by rememberSaveable { mutableStateOf(savedUsername.isNotEmpty()) }

    // Synchronize active user with repository on dynamic container start
    LaunchedEffect(savedUserId, savedUsername) {
        if (savedUserId != -1 && savedUsername.isNotEmpty()) {
            viewModel.selectActiveUser(savedUserId, savedUsername)
        }
    }

    // Level Up AlertDialog popup logic
    var activeLevelUpDialogResult by remember { mutableStateOf<LevelUpResult?>(null) }
    
    // Currently active tab switcher (No heavy Nav components to keep compile fast and fail-proof)
    var currentTab by remember { mutableStateOf("dashboard") }

    // Listen to VM toast cues
    LaunchedEffect(Unit) {
        viewModel.uiToastMessage.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    // Listen to level up animations
    LaunchedEffect(Unit) {
        viewModel.levelUpEvent.collectLatest { res ->
            activeLevelUpDialogResult = res
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .drawBehind {
                // Top-left glowing accent spot (ElectricBlue / blue-600 with 16% opacity)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ElectricBlue.copy(alpha = 0.16f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(x = -size.width * 0.1f, y = -size.height * 0.1f),
                        radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f,
                    center = androidx.compose.ui.geometry.Offset(x = -size.width * 0.1f, y = -size.height * 0.1f)
                )
                // Bottom-right glowing accent spot (NeonPurple / purple-600 with 16% opacity)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.16f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(x = size.width * 1.1f, y = size.height * 1.1f),
                        radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f,
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 1.1f, y = size.height * 1.1f)
                )
            }
    ) {
        if (!isAuthenticated) {
            LoginScreen(viewModel = viewModel, onAuthenticated = { isAuthenticated = true })
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                bottomBar = {
                    if (profileState?.hasCompletedOnboarding == true) {
                        FuturisticBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val profile = profileState

                    if (profile == null || !profile.hasCompletedOnboarding) {
                        OnboardingScreen(viewModel = viewModel)
                    } else {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            },
                            label = "TabTransition"
                        ) { targetTab ->
                            when (targetTab) {
                                "dashboard" -> DashboardScreen(
                                    viewModel = viewModel,
                                    onLogout = {
                                        viewModel.logout()
                                        isAuthenticated = false
                                    }
                                )
                                "coach" -> AICoachScreen(viewModel = viewModel)
                                "codificador" -> HabitManagerScreen(viewModel = viewModel)
                                "statistics" -> StatisticsScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // --- LEVEL UP GLOWING POPUP DIALOG ---
                    activeLevelUpDialogResult?.let { res ->
                        LevelUpDialog(
                            result = res,
                            onDismiss = { activeLevelUpDialogResult = null }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FuturisticBottomBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A))
            .drawBehind {
                // Fine high-tech top stroke line representation of border-white/5
                drawLine(
                    color = Color(0x0EFFFFFF),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding() // Secure safe area bottom rendering
            .height(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTabItem(
                isActive = currentTab == "dashboard",
                label = "Nexus",
                icon = Icons.Default.Dashboard,
                activeColor = LaserCyan,
                onClick = { onTabSelected("dashboard") }
            )

            BottomTabItem(
                isActive = currentTab == "coach",
                label = "Ia Coach",
                icon = Icons.Default.Psychology,
                activeColor = NeonPurple,
                onClick = { onTabSelected("coach") }
            )

            BottomTabItem(
                isActive = currentTab == "codificador",
                label = "Codificar",
                icon = Icons.Default.Terminal,
                activeColor = HolographicTeal,
                onClick = { onTabSelected("codificador") }
            )

            BottomTabItem(
                isActive = currentTab == "statistics",
                label = "Métricas",
                icon = Icons.Default.BarChart,
                activeColor = SolarAmber,
                onClick = { onTabSelected("statistics") }
            )
        }
    }
}

@Composable
fun BottomTabItem(
    isActive: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) activeColor else MutedSlate,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (isActive) activeColor else MutedSlate,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun LevelUpDialog(
    result: LevelUpResult,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SpaceCard, DeepVoid)
                    )
                )
                .border(2.dp, SolarAmber, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // SPARKLING CELEBRATION SHIELD
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SolarAmber.copy(alpha = 0.15f))
                        .border(2.dp, SolarAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡EVOLUCIÓN COMPLETA!",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = SolarAmber,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "SISTEMAS SUBIDOS AL NIVEL ${result.newLevel}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SoftWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                SpaceDivider(color = SolarAmber.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tu núcleo biológico universitario ha recibido un overclocking permanentemente.\n" +
                            "Todas tus defensas cognitivas se han incrementado por +2 puntos.",
                    fontSize = 12.sp,
                    color = MutedSlate,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                CyberButton(
                    text = "CONTINUAR OPERACIÓN",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = SolarAmber
                )
            }
        }
    }
}
