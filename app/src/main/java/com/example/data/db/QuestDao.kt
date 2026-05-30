package com.example.data.db

import androidx.room.*
import com.example.data.model.Quest
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests WHERE userId = :userId ORDER BY id DESC")
    fun getAllQuestsFlow(userId: Int): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllQuestsOnce(userId: Int): List<Quest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuest(quest: Quest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestsList(quests: List<Quest>)

    @Delete
    suspend fun deleteQuest(quest: Quest)

    @Query("DELETE FROM quests")
    suspend fun deleteAllQuests()
}
