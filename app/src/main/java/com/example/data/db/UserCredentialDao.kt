package com.example.data.db

import androidx.room.*
import com.example.data.model.UserCredential

@Dao
interface UserCredentialDao {
    @Query("SELECT * FROM user_credentials WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UserCredential?

    @Query("SELECT * FROM user_credentials WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserCredential?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: UserCredential)

    @Query("SELECT * FROM user_credentials")
    suspend fun getAllCredentials(): List<UserCredential>
}
