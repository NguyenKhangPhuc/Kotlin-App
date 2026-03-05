package com.example.mobilecomputing.DAO

import androidx.room.*
import com.example.mobilecomputing.entity.PostReactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostReactionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: PostReactionEntity)

    @Query("DELETE FROM post_reactions WHERE user_id = :userId AND post_id = :postId")
    suspend fun deleteReactionByUserAndPost(userId: Int, postId: Int)

    @Transaction
    @Query("SELECT * FROM post_reactions WHERE post_id = :postId")
    fun getReactionsByPostId(postId: Int): Flow<List<PostReactionEntity>>
}