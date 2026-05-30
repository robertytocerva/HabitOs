package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int = 1,
    val title: String,
    val description: String,
    val rank: String = "C-RANK", // S-RANK, A-RANK, B-RANK, C-RANK, D-RANK
    val xpReward: Int = 30,
    val creditsReward: Int = 10,
    val isCompleted: Boolean = false,
    val expiresAt: Long = System.currentTimeMillis() + 86400000 // default 24h
)
