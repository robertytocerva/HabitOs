package com.example.data.db

import androidx.room.*
import com.example.data.model.AICoachLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AICoachLogDao {
    @Query("SELECT * FROM ai_coach_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllLogsFlow(userId: Int): Flow<List<AICoachLog>>

    @Query("SELECT * FROM ai_coach_logs WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllLogsOnce(userId: Int): List<AICoachLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AICoachLog)

    @Query("DELETE FROM ai_coach_logs")
    suspend fun deleteAllLogs()
}
