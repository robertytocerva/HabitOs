package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.db.AppDatabase
import com.example.data.model.AICoachLog
import com.example.data.model.Habit
import com.example.data.model.Quest
import com.example.data.model.UserProfile
import com.example.data.repository.AppRepository
import com.example.data.repository.LevelUpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import android.content.Context
import com.example.data.model.UserCredential

class MainViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    fun selectActiveUser(userId: Int, username: String) {
        repository.setCurrentUserId(userId)
        val context = getApplication<Application>().applicationContext
        val sharedPrefs = context.getSharedPreferences("aura_habit_rpg_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("logged_in_username", username)
            .putInt("logged_in_userid", userId)
            .apply()
    }

    fun logout() {
        val context = getApplication<Application>().applicationContext
        val sharedPrefs = context.getSharedPreferences("aura_habit_rpg_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("logged_in_username", "")
            .putInt("logged_in_userid", 1)
            .apply()
        repository.setCurrentUserId(1)
    }

    fun registerNewUser(username: String, email: String, passwordRaw: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanUser = username.trim().lowercase()
            val cleanEmail = email.trim().lowercase()
            
            if (cleanUser.isBlank() || cleanEmail.isBlank() || passwordRaw.isBlank()) {
                onResult(false, "TODOS LOS CAMPOS SON REQUERIDOS.")
                return@launch
            }
            
            val existingUser = repository.getCredentialByUsername(cleanUser)
            if (existingUser != null) {
                onResult(false, "EL USUARIO YA EXISTE EN EL CÓDIGO LOCAL.")
                return@launch
            }
            val existingEmail = repository.getCredentialByEmail(cleanEmail)
            if (existingEmail != null) {
                onResult(false, "EL CORREO YA SE ENCUENTRA ENLAZADO.")
                return@launch
            }

            // Generate unique user profile ID
            val generatedProfileId = cleanUser.hashCode()
            
            // 1. Create and save login credentials in Room
            val credential = UserCredential(
                username = cleanUser,
                email = cleanEmail,
                passwordSecure = passwordRaw,
                profileId = generatedProfileId
            )
            repository.insertCredential(credential)

            // 2. Pre-create a new default UserProfile with hasCompletedOnboarding = false
            val newProfile = UserProfile(
                id = generatedProfileId,
                name = username.trim(),
                hasCompletedOnboarding = false
            )
            repository.saveUserProfile(newProfile)

            // 3. Set as currently active user
            selectActiveUser(generatedProfileId, cleanUser)
            
            onResult(true, "CUENTA REGISTRADA CORRECTAMENTE.")
        }
    }

    fun loginUser(identity: String, passwordRaw: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanId = identity.trim().lowercase()
            
            // Admin fallback
            if (cleanId == "admin" && passwordRaw == "admin") {
                val adminId = "admin".hashCode()
                val adminCred = UserCredential("admin", "admin@aura.io", "admin", adminId)
                repository.insertCredential(adminCred)
                
                val existingAdminProfile = repository.getUserProfileOnce()
                if (existingAdminProfile == null) {
                    repository.saveUserProfile(
                        UserProfile(
                            id = adminId,
                            name = "Admin",
                            hasCompletedOnboarding = true
                        )
                    )
                }
                selectActiveUser(adminId, "admin")
                onResult(true, "ACCESO CONCEDIDO // MODO ADMINISTRADOR")
                return@launch
            }
            
            val credential = repository.getCredentialByUsername(cleanId) ?: repository.getCredentialByEmail(cleanId)
            
            if (credential == null) {
                onResult(false, "CREDENCIALES ERRÓNEAS o INEXISTENTES EN LA COGNICIÓN.")
                return@launch
            }

            if (credential.passwordSecure != passwordRaw) {
                onResult(false, "CONTRASEÑA INVÁLIDA.")
                return@launch
            }

            selectActiveUser(credential.profileId, credential.username)
            onResult(true, "ACCESO CONCEDIDO // ENLACE ESTABLECIDO")
        }
    }

    // Expose flows from Repository
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<Quest>> = repository.allQuests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coachLogs: StateFlow<List<AICoachLog>> = repository.allAICoachLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state states
    private val _isGeneratingDiagnostic = MutableStateFlow(false)
    val isGeneratingDiagnostic: StateFlow<Boolean> = _isGeneratingDiagnostic.asStateFlow()

    private val _activeDiagnosticReport = MutableStateFlow<String?>(null)
    val activeDiagnosticReport: StateFlow<String?> = _activeDiagnosticReport.asStateFlow()

    private val _uiToastMessage = MutableSharedFlow<String>()
    val uiToastMessage: SharedFlow<String> = _uiToastMessage.asSharedFlow()

    private val _levelUpEvent = MutableSharedFlow<LevelUpResult>()
    val levelUpEvent: SharedFlow<LevelUpResult> = _levelUpEvent.asSharedFlow()

    // Onboarding Wizard states
    val onboardingNickname = MutableStateFlow("")
    val onboardingClass = MutableStateFlow("Cyber-Runner")
    val onboardingStress = MutableStateFlow(5)
    val onboardingSleep = MutableStateFlow(5)
    val onboardingEnergy = MutableStateFlow(5)
    val onboardingProcrastination = MutableStateFlow(5)
    val onboardingWorkhours = MutableStateFlow("Mixto")
    val onboardingGoal = MutableStateFlow("Procrastinacion")

    init {
        // Run daily reset checks / seed files occasionally here if needed
        viewModelScope.launch {
            val habitsList = repository.getAllHabitsOnce()
            // Checks if we should reset daily-completions
            val someCompletedAndOld = habitsList.any { it.isCompletedToday && System.currentTimeMillis() - it.lastCompletedTimestamp > 86400000 }
            if (someCompletedAndOld) {
                repository.resetAllHabitCompletionsDaily()
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            if (onboardingNickname.value.isBlank()) {
                _uiToastMessage.emit("Por favor, selecciona tu nombre clave.")
                return@launch
            }
            repository.initializeOnboarding(
                nickname = onboardingNickname.value.trim(),
                characterClass = onboardingClass.value,
                stress = onboardingStress.value,
                sleep = onboardingSleep.value,
                workhours = onboardingWorkhours.value.trim(),
                goal = onboardingGoal.value.trim(),
                cognitiveEnergy = onboardingEnergy.value,
                procrastinationRisk = onboardingProcrastination.value
            )
            _uiToastMessage.emit("¡Perfil Cuántico Sincronizado! Iniciando simulador...")
            triggerLiveAiNotification("Onboarding de sesión completado satisfactoriamente")
        }
    }

    fun completeHabit(habitId: Int) {
        viewModelScope.launch {
            val result = repository.completeHabit(habitId)
            if (result != null) {
                _uiToastMessage.emit("+${result.gainedXp} XP | +${result.gainedCredits} créditos")
                if (result.leveledUp) {
                    _levelUpEvent.emit(result)
                    triggerLiveAiNotification("¡Subiste al nivel ${result.newLevel}! Energía e intelecto sintonizados.")
                } else {
                    // Random lightweight AI cheers!
                    triggerLiveAiNotification("Completó con éxito el hábito enfocado en ${result.statGained}")
                }
            }
        }
    }

    fun addCustomHabit(title: String, description: String, category: String, statAffected: String, difficulty: String) {
        viewModelScope.launch {
            if (title.isBlank() || description.isBlank()) {
                _uiToastMessage.emit("Error: Rellene todos los campos para codificar el hábito.")
                return@launch
            }
            val xpRew = when (difficulty) {
                "EASY" -> 10
                "MEDIUM" -> 18
                "HARD" -> 30
                else -> 15
            }
            val credsRew = when (difficulty) {
                "EASY" -> 4
                "MEDIUM" -> 7
                "HARD" -> 12
                else -> 6
            }
            val habit = Habit(
                title = title.trim(),
                description = description.trim(),
                category = category,
                statAffected = statAffected,
                difficulty = difficulty,
                xpReward = xpRew,
                creditsReward = credsRew,
                statGainValue = if (difficulty == "HARD") 4 else if (difficulty == "MEDIUM") 2 else 1
            )
            repository.insertOrUpdateHabit(habit)
            _uiToastMessage.emit("Hábito '${title}' codificado con éxito en la HUD")
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            repository.deleteHabitById(habitId)
            _uiToastMessage.emit("Hábito desinstalado de la red.")
        }
    }

    fun completeQuest(questId: Int) {
        viewModelScope.launch {
            val result = repository.completeQuest(questId)
            if (result != null) {
                _uiToastMessage.emit("Misión Completada: +${result.gainedXp} XP | +${result.gainedCredits} créditos")
                if (result.leveledUp) {
                    _levelUpEvent.emit(result)
                }
            }
        }
    }

    fun runNeuralDiagnostic() {
        val user = userProfile.value ?: return
        val currentHabits = habits.value
        viewModelScope.launch {
            _isGeneratingDiagnostic.value = true
            _activeDiagnosticReport.value = "Calculando trayectorias neurales... Solicitando datos de constelación..."
            
            val prompt = GeminiClient.createNeuralDiagnosticPrompt(user, currentHabits)
            val aiResponse = GeminiClient.generateAiResponse(prompt)
            
            _activeDiagnosticReport.value = aiResponse
            _isGeneratingDiagnostic.value = false

            // Log diagnostic history in DB
            val coachLog = AICoachLog(
                triggerContext = "SCAN",
                userSnapshotStats = "Nivel ${user.level}, DIS ${user.statDiscipline}, FOC ${user.statFocus}",
                aiMessage = aiResponse,
                notificationTitle = "Análisis de Core completado"
            )
            repository.insertAICoachLog(coachLog)
        }
    }

    fun clearDiagnosticLogs() {
        viewModelScope.launch {
            repository.clearAllAICoachLogs()
            _uiToastMessage.emit("Registros cuánticos limpiados.")
        }
    }

    // Chat with AI states
    private val _isAskingAi = MutableStateFlow(false)
    val isAskingAi: StateFlow<Boolean> = _isAskingAi.asStateFlow()

    private val _aiCoachReply = MutableStateFlow<String?>(null)
    val aiCoachReply: StateFlow<String?> = _aiCoachReply.asStateFlow()

    fun askAiFree(question: String) {
        val user = userProfile.value ?: return
        if (question.isBlank()) return
        viewModelScope.launch {
            _isAskingAi.value = true
            _aiCoachReply.value = "Consultando con la red neural... Sintonizando respuestas adaptativas..."
            
            val systemContext = """
                Estás actuando como el Coach IA Central de HabitOS, el sistema operativo de hábitos gamificados para estudiantes universitarios.
                Estás respondiendo a una pregunta libre del estudiante en su chat de terminal táctil.
                
                Perfil del Operario Estudiante:
                - Nombre: ${user.name}
                - Clase: ${user.getClassTitleSpanish()}
                - Nivel: ${user.level} (Atributos: Disciplina=${user.statDiscipline}, Enfoque=${user.statFocus}, Energía=${user.statEnergy}, Mente=${user.statMind}, Cuerpo=${user.statBody})
                
                Pregunta del Estudiante: "$question"
                
                Por favor, responde en ESPAÑOL, con una vibra completamente tecnológica, inmersiva, cyberpunk y amigable. Ofrece sugerencias pragmáticas y realistas para la vida estudiantil.
                Sé conciso (máximo 150-180 palabras desglosadas con viñetas elegantes o iconos de estado), motivador y directo al grano táctico. No abuses de explicaciones largas.
            """.trimIndent()
            
            val aiResponse = GeminiClient.generateAiResponse(systemContext)
            _aiCoachReply.value = aiResponse
            _isAskingAi.value = false

            // Store chat log in database
            val chatLog = AICoachLog(
                triggerContext = "CHAT",
                userSnapshotStats = "Pregunta: ${question.take(25)}...",
                aiMessage = "PREGUNTA: $question\n\nCOACH IA RESPUESTA:\n$aiResponse",
                notificationTitle = "Pregunta Libre"
            )
            repository.insertAICoachLog(chatLog)
        }
    }

    fun clearAiCoachReply() {
        _aiCoachReply.value = null
    }

    fun recommendNewHabitWithAi(category: String, onRecommendationParsed: (String, String, String, String) -> Unit) {
        val user = userProfile.value ?: return
        viewModelScope.launch {
            _isGeneratingDiagnostic.value = true
            val prompt = GeminiClient.recommendNewHabitPrompt(user, category)
            val aiResponse = GeminiClient.generateAiResponse(prompt)
            _isGeneratingDiagnostic.value = false

            // Quick parsing
            var habitName = "Recarga Energética Holográfica"
            var habitDesc = "Sintoniza tu conciencia durante 10 minutos."
            var stat = "ENERGY"
            var diff = "MEDIUM"

            try {
                val lines = aiResponse.lines()
                for (line in lines) {
                    if (line.startsWith("[NOMBRE]:")) {
                        habitName = line.substringAfter("[NOMBRE]:").trim()
                    } else if (line.startsWith("[DESCRIPCION]:")) {
                        habitDesc = line.substringAfter("[DESCRIPCION]:").trim()
                    } else if (line.startsWith("[ESTADISTICA]:")) {
                        val parsedStat = line.substringAfter("[ESTADISTICA]:").trim().uppercase()
                        if (listOf("DISCIPLINE", "FOCUS", "ENERGY", "MIND", "BODY").contains(parsedStat)) {
                            stat = parsedStat
                        }
                    } else if (line.startsWith("[DIFICULTAD]:")) {
                        val parsedDiff = line.substringAfter("[DIFICULTAD]:").trim().uppercase()
                        if (listOf("EASY", "MEDIUM", "HARD").contains(parsedDiff)) {
                            diff = parsedDiff
                        }
                    }
                }
            } catch (e: Exception) {
                // Keep default and inform
            }

            onRecommendationParsed(habitName, habitDesc, stat, diff)
        }
    }

    private fun triggerLiveAiNotification(eventContext: String) {
        val user = userProfile.value ?: return
        viewModelScope.launch {
            val prompt = GeminiClient.notificationPrompt(user, eventContext)
            val notificationText = GeminiClient.generateAiResponse(prompt)
            _uiToastMessage.emit("📡 COACH IA: $notificationText")
        }
    }

    fun resetWholeGame() {
        viewModelScope.launch {
            repository.deleteUserProfile()
            repository.clearAllAICoachLogs()
            _uiToastMessage.emit("Reinicio Completo... Recargando de fábrica")
        }
    }

    fun buyTitle(title: String, cost: Int) {
        val user = userProfile.value ?: return
        if (user.credits < cost) {
            viewModelScope.launch { _uiToastMessage.emit("Créditos insuficientes en la red.") }
            return
        }
        viewModelScope.launch {
            val updated = user.copy(
                credits = user.credits - cost,
                purchasedTitle = title
            )
            repository.saveUserProfile(updated)
            _uiToastMessage.emit("¡Título '$title' desbloqueado y equipado!")
        }
    }

    fun buyTheme(themeName: String, cost: Int) {
        val user = userProfile.value ?: return
        if (user.credits < cost) {
            viewModelScope.launch { _uiToastMessage.emit("Créditos insuficientes en la red.") }
            return
        }
        viewModelScope.launch {
            val updated = user.copy(
                credits = user.credits - cost,
                purchasedTheme = themeName
            )
            repository.saveUserProfile(updated)
            _uiToastMessage.emit("¡Personalización de Neón '$themeName' activa!")
        }
    }

    fun purchaseStatBoost(statName: String, cost: Int) {
        val user = userProfile.value ?: return
        if (user.credits < cost) {
            viewModelScope.launch { _uiToastMessage.emit("Créditos insuficientes en la red.") }
            return
        }
        viewModelScope.launch {
            val updated = when (statName) {
                "DISCIPLINE" -> user.copy(credits = user.credits - cost, statDiscipline = (user.statDiscipline + 5).coerceAtMost(100))
                "FOCUS" -> user.copy(credits = user.credits - cost, statFocus = (user.statFocus + 5).coerceAtMost(100))
                "ENERGY" -> user.copy(credits = user.credits - cost, statEnergy = (user.statEnergy + 5).coerceAtMost(100))
                "MIND" -> user.copy(credits = user.credits - cost, statMind = (user.statMind + 5).coerceAtMost(100))
                "BODY" -> user.copy(credits = user.credits - cost, statBody = (user.statBody + 5).coerceAtMost(100))
                else -> user
            }
            repository.saveUserProfile(updated)
            _uiToastMessage.emit("¡Atributo $statName incrementado en +5 unidades!")
            triggerLiveAiNotification("Adquirió un potenciador cyber-sensorial de $statName utilizando créditos de red")
        }
    }

    fun purchaseXpInjection(xpAmount: Int, cost: Int) {
        val user = userProfile.value ?: return
        if (user.credits < cost) {
            viewModelScope.launch { _uiToastMessage.emit("Créditos insuficientes en la red.") }
            return
        }
        viewModelScope.launch {
            var totalXp = user.xp + xpAmount
            var level = user.level
            var xpToNext = user.xpToNextLevel
            var leveledUp = false

            var newDisc = user.statDiscipline
            var newFocus = user.statFocus
            var newEnergy = user.statEnergy
            var newMind = user.statMind
            var newBody = user.statBody

            while (totalXp >= xpToNext) {
                totalXp -= xpToNext
                level += 1
                xpToNext = level * 100 + 50
                leveledUp = true
                newDisc = (newDisc + 2).coerceAtMost(100)
                newFocus = (newFocus + 2).coerceAtMost(100)
                newEnergy = (newEnergy + 2).coerceAtMost(100)
                newMind = (newMind + 2).coerceAtMost(100)
                newBody = (newBody + 2).coerceAtMost(100)
            }

            val updated = user.copy(
                credits = user.credits - cost,
                xp = totalXp,
                level = level,
                xpToNextLevel = xpToNext,
                statDiscipline = newDisc,
                statFocus = newFocus,
                statEnergy = newEnergy,
                statMind = newMind,
                statBody = newBody
            )
            repository.saveUserProfile(updated)
            _uiToastMessage.emit("¡Inyección de +$xpAmount XP activa!")
            if (leveledUp) {
                _levelUpEvent.emit(LevelUpResult(xpAmount, 0, true, level, "Todas", 2))
                triggerLiveAiNotification("¡Exposición cuántica acelerada! Subiste de nivel mediante hack XP.")
            }
        }
    }
}

// Factory to instantiate with Database/Repository
class MainViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
