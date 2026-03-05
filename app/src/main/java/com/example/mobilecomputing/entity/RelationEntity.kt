package com.example.mobilecomputing.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
@Entity(
    tableName = "relationships",
    indices = [
        Index(value = ["follower_id", "following_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["follower_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["following_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RelationshipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "follower_id")
    val followerId: Int,

    @ColumnInfo(name = "following_id")
    val followingId: Int
)