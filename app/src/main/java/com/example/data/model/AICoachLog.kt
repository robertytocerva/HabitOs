package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_coach_logs")
data class AICoachLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val triggerContext: String, // "SCAN", "LEVEL_UP", "ADVICE", "HABIT_RECOMMEND"
    val userSnapshotStats: String, // "LVL 3, DIS 12, FOC 15, ENE 10" etc.
    val aiMessage: String,
    val notificationTitle: String = "Consejo de la IA"
)
