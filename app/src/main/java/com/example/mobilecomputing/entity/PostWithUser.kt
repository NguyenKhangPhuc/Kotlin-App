package com.example.mobilecomputing.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithUser(
    @Embedded
    val post: PostEntity,

    @Relation(
        entity = UserProfileEntity::class,
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: UserPublicInfo
)