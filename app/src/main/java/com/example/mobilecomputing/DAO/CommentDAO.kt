package com.example.mobilecomputing.DAO

import androidx.room.*
import com.example.mobilecomputing.entity.CommentEntity
import com.example.mobilecomputing.entity.CommentWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Int)

    @Transaction
    @Query("SELECT * FROM comments WHERE post_id = :postId")
    fun getCommentsWithUserByPostId(postId: Int): Flow<List<CommentWithUser>>
}

