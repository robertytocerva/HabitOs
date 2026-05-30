package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int = 1,
    val title: String,
    val category: String, // ACADEMIC, PHYSICAL, MENTAL, SLEEP, HYDRATION, TIME_CONTROL
    val description: String,
    val difficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val xpReward: Int = 15,
    val creditsReward: Int = 5,
    val statAffected: String = "DISCIPLINE", // DISCIPLINE, FOCUS, ENERGY, MIND, BODY
    val statGainValue: Int = 2,
    val streak: Int = 0,
    val totalCompletions: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedTimestamp: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getCategoryColorHex(): String {
        return when (category) {
            "ACADEMIC" -> "#0072FF" // Electric Blue
            "PHYSICAL" -> "#00FF87" // Neon Green
            "MENTAL" -> "#BD00FF" // Neon Purple
            "SLEEP" -> "#00E5FF" // Laser Cyan
            "HYDRATION" -> "#00F5D4" // Holographic Teal
            else -> "#FFB03A" // Amber Solar
        }
    }
    
    fun getCategorySpanish(): String {
        return when (category) {
            "ACADEMIC" -> "Estudio / Académico"
            "PHYSICAL" -> "Salud Física"
            "MENTAL" -> "Salud Mental"
            "SLEEP" -> "Sueño y Descanso"
            "HYDRATION" -> "Hidratación"
            "TIME_CONTROL" -> "Control de Tiempo"
            else -> "Hábito Integral"
        }
    }
}
