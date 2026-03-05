package com.example.mobilecomputing.entity


import androidx.room.Embedded
import androidx.room.Relation

data class RelationWithUser(
    @Embedded
    val relationship: RelationshipEntity,

    @Relation(
        entity = UserProfileEntity::class,
        parentColumn = "follower_id",
        entityColumn = "id"
    )
    val follower: UserPublicInfo,
    @Relation(
        entity = UserProfileEntity::class,
        parentColumn = "following_id",
        entityColumn = "id"
    )
    val following: UserPublicInfo
)