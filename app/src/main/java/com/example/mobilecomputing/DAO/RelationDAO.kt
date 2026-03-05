package com.example.mobilecomputing.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mobilecomputing.entity.RelationWithUser
import com.example.mobilecomputing.entity.RelationshipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(relationship: RelationshipEntity): Long

    @Query("DELETE FROM relationships WHERE follower_id = :followerId AND following_id = :followingId")
    suspend fun unfollow(followerId: Int, followingId: Int)

    @Transaction
    @Query("SELECT * FROM relationships WHERE follower_id = :followerId")
    fun getFollowings(followerId: Int): Flow<List<RelationWithUser>>

    @Transaction
    @Query("SELECT * FROM relationships WHERE following_id = :followingId")
    fun getFollowers(followingId: Int): Flow<List<RelationWithUser>>
}