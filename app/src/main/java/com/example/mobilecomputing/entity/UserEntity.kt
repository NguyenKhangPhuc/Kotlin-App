package com.example.mobilecomputing.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String?,
    val username: String?,
    val password: String?,
    val imagePath: String?
)
