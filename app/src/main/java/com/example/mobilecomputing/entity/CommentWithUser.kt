package com.example.mobilecomputing.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CommentWithUser(
    @Embedded
    val comment: CommentEntity,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: UserProfileEntity
)