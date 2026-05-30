package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val characterClass: String = "Cyber-Runner", // Cyber-Runner, Codex-Scribe, Mind-Sentry, Nano-Titan
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val credits: Int = 50,
    val streak: Int = 0,
    
    // RPG Personal Stats (1-100 scale)
    val statDiscipline: Int = 10,  // Affects organization/consistency
    val statFocus: Int = 10,       // Affects academic study sessions
    val statEnergy: Int = 10,      // Affects overall stamina and hydration
    val statMind: Int = 10,        // Affects stress relief/mental health
    val statBody: Int = 10,        // Affects fitness/sleep

    val hasCompletedOnboarding: Boolean = false,
    val onboardingStressLevel: Int = 5,
    val onboardingSleepQuality: Int = 5,
    val onboardingWorkhours: String = "",
    val primaryGoal: String = "",
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val purchasedTitle: String = "",
    val purchasedTheme: String = ""
) {
    fun getClassTitleSpanish(): String {
        return when (characterClass) {
            "Codex-Scribe" -> "Escriba del Códice (Enfoque Académico)"
            "Cyber-Runner" -> "Cyber-Runner (Productor ultra veloz)"
            "Mind-Sentry" -> "Baluarte Mental (Manejo del Estrés)"
            "Nano-Titan" -> "Nano-Titán (Entrenamiento y Resistencia Física)"
            else -> "Cyber-Runner"
        }
    }

    fun getClassDescriptionSpanish(): String {
        return when (characterClass) {
            "Codex-Scribe" -> "Dominas la teoría, investigas algoritmos complejos y posees una concentración académica legendaria."
            "Cyber-Runner" -> "Tu especialidad es hackear el tiempo. Destruyes la procrastinación organizando tus bloques de estudio."
            "Mind-Sentry" -> "Maestro del autocontrol emocional. Tu paz mental brilla resplandeciente frente a los exámenes de fin de semestre."
            "Nano-Titan" -> "Posees una fuerza corporal indomable. Conviertes el sudor y la disciplina física en energía bruta para tu cerebro."
            else -> "Un guerrero integral del espacio universitario."
        }
    }
}
