package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.UserProfile
import com.example.data.model.Habit
import com.example.data.model.Quest
import com.example.data.model.AICoachLog
import com.example.data.model.UserCredential

@Database(
    entities = [
        UserProfile::class,
        Habit::class,
        Quest::class,
        AICoachLog::class,
        UserCredential::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userCredentialDao(): UserCredentialDao
    abstract fun habitDao(): HabitDao
    abstract fun questDao(): QuestDao
    abstract fun aiCoachLogDao(): AICoachLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aura_habit_rpg_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
