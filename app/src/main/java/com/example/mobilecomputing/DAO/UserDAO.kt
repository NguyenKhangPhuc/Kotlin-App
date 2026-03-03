package com.example.mobilecomputing.DAO

import androidx.room.*
import com.example.mobilecomputing.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getProfileFlow(id: Int): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE email = :email")
    fun getUserProfile(email: String): UserProfileEntity

    @Query("UPDATE user_profile SET username = :name WHERE id = :id")
    fun updateUsername(name: String, id: Int)

    @Query("UPDATE user_profile SET imagePath = :path WHERE id = :id")
    fun updateImagePath(path: String, id: Int)
}
