package com.example.data.repository

import com.example.data.db.UserDao
import com.example.data.db.UserCredentialDao
import com.example.data.db.HabitDao
import com.example.data.db.QuestDao
import com.example.data.db.AICoachLogDao
import com.example.data.model.UserProfile
import com.example.data.model.UserCredential
import com.example.data.model.Habit
import com.example.data.model.Quest
import com.example.data.model.AICoachLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.roundToInt

class AppRepository(
    private val userDao: UserDao,
    private val userCredentialDao: UserCredentialDao,
    private val habitDao: HabitDao,
    private val questDao: QuestDao,
    private val aiCoachLogDao: AICoachLogDao
) {
    private val _currentUserId = MutableStateFlow<Int>(1)
    val currentUserId: StateFlow<Int> = _currentUserId.asStateFlow()

    fun setCurrentUserId(userId: Int) {
        _currentUserId.value = userId
    }

    fun getCurrentUserId(): Int = _currentUserId.value

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: Flow<UserProfile?> = _currentUserId.flatMapLatest { userId ->
        userDao.getUserProfileFlow(userId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allHabits: Flow<List<Habit>> = _currentUserId.flatMapLatest { userId ->
        habitDao.getAllHabitsFlow(userId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allQuests: Flow<List<Quest>> = _currentUserId.flatMapLatest { userId ->
        questDao.getAllQuestsFlow(userId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allAICoachLogs: Flow<List<AICoachLog>> = _currentUserId.flatMapLatest { userId ->
        aiCoachLogDao.getAllLogsFlow(userId)
    }

    suspend fun getCredentialByUsername(username: String): UserCredential? =
        userCredentialDao.getByUsername(username)

    suspend fun getCredentialByEmail(email: String): UserCredential? =
        userCredentialDao.getByEmail(email)

    suspend fun insertCredential(credential: UserCredential) {
        userCredentialDao.insertCredential(credential)
    }

    suspend fun getUserProfileOnce(): UserProfile? = userDao.getUserProfileOnce(_currentUserId.value)
    suspend fun getAllHabitsOnce(): List<Habit> = habitDao.getAllHabitsOnce(_currentUserId.value)
    suspend fun getAllQuestsOnce(): List<Quest> = questDao.getAllQuestsOnce(_currentUserId.value)

    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.insertOrUpdateUserProfile(profile.copy(id = _currentUserId.value))
    }

    suspend fun deleteUserProfile() {
        userDao.deleteUserProfile(_currentUserId.value)
    }

    suspend fun insertOrUpdateHabit(habit: Habit) {
        habitDao.insertOrUpdateHabit(habit.copy(userId = _currentUserId.value))
    }

    suspend fun deleteHabitById(id: Int) {
        habitDao.deleteHabitById(id)
    }

    suspend fun insertOrUpdateQuest(quest: Quest) {
        questDao.insertOrUpdateQuest(quest.copy(userId = _currentUserId.value))
    }

    suspend fun deleteQuest(quest: Quest) {
        questDao.deleteQuest(quest.copy(userId = _currentUserId.value))
    }

    suspend fun insertAICoachLog(log: AICoachLog) {
        aiCoachLogDao.insertLog(log.copy(userId = _currentUserId.value))
    }

    suspend fun clearAllAICoachLogs() {
        aiCoachLogDao.deleteAllLogs()
    }

    suspend fun initializeOnboarding(
        nickname: String,
        characterClass: String,
        stress: Int,
        sleep: Int,
        workhours: String,
        goal: String,
        cognitiveEnergy: Int = 5,
        procrastinationRisk: Int = 5
    ) {
        // Base stats based on Class for initial stats immersion
        val (disc, focus, energy, mind, body) = when (characterClass) {
            "Codex-Scribe" -> listOf(15, 20, 10, 12, 8)
            "Cyber-Runner" -> listOf(18, 14, 12, 11, 10)
            "Mind-Sentry" -> listOf(12, 13, 10, 20, 10)
            "Nano-Titan" -> listOf(12, 10, 15, 8, 20)
            else -> listOf(12, 12, 12, 12, 12)
        }

        val baseProfile = UserProfile(
            id = _currentUserId.value,
            name = nickname,
            characterClass = characterClass,
            hasCompletedOnboarding = true,
            onboardingStressLevel = stress,
            onboardingSleepQuality = sleep,
            onboardingWorkhours = workhours,
            primaryGoal = goal,
            statDiscipline = disc,
            statFocus = focus,
            statEnergy = energy,
            statMind = mind,
            statBody = body,
            level = 1,
            xp = 0,
            xpToNextLevel = 100,
            credits = 55
        )
        userDao.insertOrUpdateUserProfile(baseProfile)

        // Generate initial class habits
        val studyTitle = when (workhours) {
            "Mañana" -> "Bloque de Mañana: 30m Estudio Profundo"
            "Tarde" -> "Bloque de Tarde: 30m Estudio Profundo"
            "Noche" -> "Bloque del Búho: 30m Estudio Nocturno"
            else -> "Estudiar en bloques de 30 mins (Pomodoro)"
        }

        val initialHabits = mutableListOf(
            Habit(
                title = studyTitle,
                category = "ACADEMIC",
                description = "Enfócate sin distracciones en tu bloque elegido.",
                difficulty = "MEDIUM",
                xpReward = 20,
                creditsReward = 8,
                statAffected = "FOCUS",
                statGainValue = 3
            ),
            Habit(
                title = "Beber 2 litros de agua",
                category = "HYDRATION",
                description = "Mantén tu cerebro hidratado para rendir al máximo.",
                difficulty = "EASY",
                xpReward = 10,
                creditsReward = 4,
                statAffected = "ENERGY",
                statGainValue = 1
            )
        )

        // Personalized: If stress is high
        if (stress >= 6) {
            initialHabits.add(
                Habit(
                    title = "Respiración 4-7-8 contra Estrés",
                    category = "MENTAL",
                    description = "Reduce la sobrecarga mental (Inhalar 4s, Aguantar 7s, Exhalar 8s).",
                    difficulty = "EASY",
                    xpReward = 15,
                    creditsReward = 5,
                    statAffected = "MIND",
                    statGainValue = 2
                )
            )
        } else {
            initialHabits.add(
                Habit(
                    title = "Meditar 5 minutos contra el estrés",
                    category = "MENTAL",
                    description = "Práctica de respiración profunda consciente.",
                    difficulty = "EASY",
                    xpReward = 12,
                    creditsReward = 5,
                    statAffected = "MIND",
                    statGainValue = 2
                )
            )
        }

        // Personalized: If sleep is poor
        if (sleep <= 5) {
            initialHabits.add(
                Habit(
                    title = "Apagar pantallas 30 min antes de dormir",
                    category = "SLEEP",
                    description = "Reduce la luz azul para estabilizar la melatonina.",
                    difficulty = "MEDIUM",
                    xpReward = 22,
                    creditsReward = 9,
                    statAffected = "BODY",
                    statGainValue = 3
                )
            )
        } else {
            initialHabits.add(
                Habit(
                    title = "Dormir a una hora consistente",
                    category = "SLEEP",
                    description = "Ordena tus ciclos circadianos universitarios.",
                    difficulty = "MEDIUM",
                    xpReward = 15,
                    creditsReward = 6,
                    statAffected = "BODY",
                    statGainValue = 2
                )
            )
        }

        // Personalized: Cognitive Energy is low
        if (cognitiveEnergy <= 5) {
            initialHabits.add(
                Habit(
                    title = "Pausa Activa: Estiramiento de 5 mins",
                    category = "PHYSICAL",
                    description = "Despeja tu mente saliendo de la silla académica.",
                    difficulty = "EASY",
                    xpReward = 12,
                    creditsReward = 4,
                    statAffected = "ENERGY",
                    statGainValue = 2
                )
            )
        }

        // Personalized: Procrastination Risk is high
        if (procrastinationRisk >= 6) {
            initialHabits.add(
                Habit(
                    title = "Arrancar de una (Regla de 5s)",
                    category = "TIME_CONTROL",
                    description = "Cuenta 5-4-3-2-1 y abre de inmediato tus apuntes de estudio.",
                    difficulty = "EASY",
                    xpReward = 18,
                    creditsReward = 6,
                    statAffected = "DISCIPLINE",
                    statGainValue = 3
                )
            )
        }

        // Personalized: Goals representation
        when (goal) {
            "Tesis" -> {
                initialHabits.add(
                    Habit(
                        title = "Escribir 1 página / Avance de Tesis",
                        category = "ACADEMIC",
                        description = "Crea progreso continuo para tu fase final de grado.",
                        difficulty = "HARD",
                        xpReward = 35,
                        creditsReward = 15,
                        statAffected = "FOCUS",
                        statGainValue = 4
                    )
                )
            }
            "Materias" -> {
                initialHabits.add(
                    Habit(
                        title = "Resolver 1 ejercicio práctico complejo",
                        category = "ACADEMIC",
                        description = "Enfréntate a las materias de filtro sin dudar.",
                        difficulty = "HARD",
                        xpReward = 32,
                        creditsReward = 14,
                        statAffected = "FOCUS",
                        statGainValue = 4
                    )
                )
            }
            "Salud" -> {
                initialHabits.add(
                    Habit(
                        title = "Entrenamiento físico o cardio (20m)",
                        category = "PHYSICAL",
                        description = "Incrementa tu resistencia somática y salud cerebral.",
                        difficulty = "MEDIUM",
                        xpReward = 25,
                        creditsReward = 10,
                        statAffected = "BODY",
                        statGainValue = 3
                    )
                )
            }
            "Procrastinacion" -> {
                initialHabits.add(
                    Habit(
                        title = "Declarar 3 metas del día al despertar",
                        category = "TIME_CONTROL",
                        description = "Escribe tus objetivos y ejecútalos con precisión militar.",
                        difficulty = "EASY",
                        xpReward = 15,
                        creditsReward = 5,
                        statAffected = "DISCIPLINE",
                        statGainValue = 2
                    )
                )
            }
        }

        // Class-specific custom starting habits
        when (characterClass) {
            "Codex-Scribe" -> {
                initialHabits.add(
                    Habit(
                        title = "Repasar temario difícil antes de dormir",
                        category = "ACADEMIC",
                        description = "Crea conexiones neurales fuertes durante el sueño.",
                        difficulty = "HARD",
                        xpReward = 30,
                        creditsReward = 12,
                        statAffected = "FOCUS",
                        statGainValue = 4
                    )
                )
            }
            "Cyber-Runner" -> {
                initialHabits.add(
                    Habit(
                        title = "Planificar agenda del día siguiente",
                        category = "TIME_CONTROL",
                        description = "Evita la fatiga de decisión hackeando tu calendario.",
                        difficulty = "EASY",
                        xpReward = 15,
                        creditsReward = 5,
                        statAffected = "DISCIPLINE",
                        statGainValue = 2
                    )
                )
            }
            "Mind-Sentry" -> {
                initialHabits.add(
                    Habit(
                        title = "Bitácora mental: Escribir 3 gratitudes",
                        category = "MENTAL",
                        description = "Refuerza la mentalidad positiva frente a presiones.",
                        difficulty = "EASY",
                        xpReward = 15,
                        creditsReward = 6,
                        statAffected = "MIND",
                        statGainValue = 3
                    )
                )
            }
            "Nano-Titan" -> {
                initialHabits.add(
                    Habit(
                        title = "30 minutos de ejercicio físico cardiorrespiratorio",
                        category = "PHYSICAL",
                        description = "Oxigena la corteza prefrontal prestando atención al cuerpo.",
                        difficulty = "HARD",
                        xpReward = 35,
                        creditsReward = 15,
                        statAffected = "BODY",
                        statGainValue = 4
                    )
                )
            }
        }

        val habitsWithUser = initialHabits.map { it.copy(userId = _currentUserId.value) }
        habitDao.insertHabitsList(habitsWithUser)

        // Setup custom starter daily quest based on goal
        val goalQuestName = when (goal) {
            "Tesis" -> "Hito de Grado: Avance de Proyecto"
            "Materias" -> "Héroe Académico: Materias Críticas"
            "Salud" -> "Código Sanitario: Templanza corporal"
            else -> "Cazador de Procrastinación"
        }

        val initialQuests = listOf(
            Quest(
                title = "Misión de Reconocimiento IA",
                description = "Inicia un diagnóstico neural con la IA en la pestaña de Coach para sintonizar tus objetivos.",
                rank = "D-RANK",
                xpReward = 25,
                creditsReward = 8
            ),
            Quest(
                title = goalQuestName,
                description = "Completa 2 hábitos clave alineados con tu objetivo principal hoy.",
                rank = "B-RANK",
                xpReward = 45,
                creditsReward = 15
            )
        )
        val questsWithUser = initialQuests.map { it.copy(userId = _currentUserId.value) }
        questDao.insertQuestsList(questsWithUser)
    }

    suspend fun completeHabit(habitId: Int): LevelUpResult? {
        val habit = habitDao.getHabitById(habitId) ?: return null
        val profile = getUserProfileOnce() ?: return null

        val isRecompletionOfToday = habit.isCompletedToday
        
        // Update habit completions
        val newStreak = if (isRecompletionOfToday) habit.streak else habit.streak + 1
        val newTotal = if (isRecompletionOfToday) habit.totalCompletions else habit.totalCompletions + 1
        
        val updatedHabit = habit.copy(
            isCompletedToday = true,
            streak = newStreak,
            totalCompletions = newTotal,
            lastCompletedTimestamp = System.currentTimeMillis()
        )
        habitDao.insertOrUpdateHabit(updatedHabit)

        // Only give XP, credits, and stats if completing for the FIRST time today
        if (isRecompletionOfToday) {
            return null
        }

        // Apply attribute additions
        var newDisc = profile.statDiscipline
        var newFocus = profile.statFocus
        var newEnergy = profile.statEnergy
        var newMind = profile.statMind
        var newBody = profile.statBody

        when (habit.statAffected) {
            "DISCIPLINE" -> newDisc = (newDisc + habit.statGainValue).coerceAtMost(100)
            "FOCUS" -> newFocus = (newFocus + habit.statGainValue).coerceAtMost(100)
            "ENERGY" -> newEnergy = (newEnergy + habit.statGainValue).coerceAtMost(100)
            "MIND" -> newMind = (newMind + habit.statGainValue).coerceAtMost(100)
            "BODY" -> newBody = (newBody + habit.statGainValue).coerceAtMost(100)
        }

        // Apply gold/XP rewards
        val gainedXp = habit.xpReward
        val gainedCredits = habit.creditsReward

        var totalXp = profile.xp + gainedXp
        var level = profile.level
        var xpToNext = profile.xpToNextLevel
        var leveledUp = false

        // Check level up structure
        while (totalXp >= xpToNext) {
            totalXp -= xpToNext
            level += 1
            xpToNext = level * 100 + 50
            leveledUp = true
            // Boost all stats by 2 as structural level bonus
            newDisc = (newDisc + 2).coerceAtMost(100)
            newFocus = (newFocus + 2).coerceAtMost(100)
            newEnergy = (newEnergy + 2).coerceAtMost(100)
            newMind = (newMind + 2).coerceAtMost(100)
            newBody = (newBody + 2).coerceAtMost(100)
        }

        val updatedProfile = profile.copy(
            level = level,
            xp = totalXp,
            xpToNextLevel = xpToNext,
            credits = profile.credits + gainedCredits,
            statDiscipline = newDisc,
            statFocus = newFocus,
            statEnergy = newEnergy,
            statMind = newMind,
            statBody = newBody,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        userDao.insertOrUpdateUserProfile(updatedProfile)

        return LevelUpResult(
            gainedXp = gainedXp,
            gainedCredits = gainedCredits,
            leveledUp = leveledUp,
            newLevel = level,
            statGained = habit.statAffected,
            statGainedAmount = habit.statGainValue
        )
    }

    suspend fun completeQuest(questId: Int): LevelUpResult? {
        val quests = getAllQuestsOnce()
        val quest = quests.find { it.id == questId } ?: return null
        if (quest.isCompleted) return null

        val updatedQuest = quest.copy(isCompleted = true)
        questDao.insertOrUpdateQuest(updatedQuest)

        val profile = getUserProfileOnce() ?: return null

        val gainedXp = quest.xpReward
        val gainedCredits = quest.creditsReward

        var totalXp = profile.xp + gainedXp
        var level = profile.level
        var xpToNext = profile.xpToNextLevel
        var leveledUp = false

        var newDisc = profile.statDiscipline
        var newFocus = profile.statFocus
        var newEnergy = profile.statEnergy
        var newMind = profile.statMind
        var newBody = profile.statBody

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

        val updatedProfile = profile.copy(
            level = level,
            xp = totalXp,
            xpToNextLevel = xpToNext,
            credits = profile.credits + gainedCredits,
            statDiscipline = newDisc,
            statFocus = newFocus,
            statEnergy = newEnergy,
            statMind = newMind,
            statBody = newBody,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        userDao.insertOrUpdateUserProfile(updatedProfile)

        return LevelUpResult(
            gainedXp = gainedXp,
            gainedCredits = gainedCredits,
            leveledUp = leveledUp,
            newLevel = level,
            statGained = "TODAS (Bonus)",
            statGainedAmount = if (leveledUp) 2 else 0
        )
    }

    suspend fun resetAllHabitCompletionsDaily() {
        // Run this when starting a new day or forcing recalculations
        val habits = getAllHabitsOnce()
        val resetHabits = habits.map {
            it.copy(isCompletedToday = false)
        }
        habitDao.insertHabitsList(resetHabits)
    }
}

data class LevelUpResult(
    val gainedXp: Int,
    val gainedCredits: Int,
    val leveledUp: Boolean,
    val newLevel: Int,
    val statGained: String,
    val statGainedAmount: Int
)
