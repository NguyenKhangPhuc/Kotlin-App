package com.example.mobilecomputing.DAO

import androidx.room.*
import com.example.mobilecomputing.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun getProfileFlow(): Flow<UserProfileEntity?>

    @Query("UPDATE user_profile SET username = :name WHERE id = 0")
    fun updateUsername(name: String)

    @Query("UPDATE user_profile SET imagePath = :path WHERE id = 0")
    fun updateImagePath(path: String)
}
