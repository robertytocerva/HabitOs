package com.example.data.db

import androidx.room.*
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    fun getUserProfileFlow(userId: Int): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun getUserProfileOnce(userId: Int): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profile WHERE id = :userId")
    suspend fun deleteUserProfile(userId: Int)
}
