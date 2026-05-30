package com.example.data.db

import androidx.room.*
import com.example.data.model.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY id DESC")
    fun getAllHabitsFlow(userId: Int): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllHabitsOnce(userId: Int): List<Habit>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabitById(id: Int): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitsList(habits: List<Habit>)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Int)
}
