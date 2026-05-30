package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credentials")
data class UserCredential(
    @PrimaryKey val username: String,
    val email: String,
    val passwordSecure: String,
    val profileId: Int
)
