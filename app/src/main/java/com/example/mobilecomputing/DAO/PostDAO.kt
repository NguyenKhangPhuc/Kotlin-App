package com.example.mobilecomputing.DAO

import androidx.room.*
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.PostWithUser
import com.example.mobilecomputing.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPostsFlow(): Flow<List<PostWithUser>>

    @Query("SELECT * FROM posts WHERE user_id = :userId")
    fun getPostsByUserId(userId: Int): Flow<List<PostWithUser>>

    @Delete
    suspend fun deletePost(post: PostEntity)
}