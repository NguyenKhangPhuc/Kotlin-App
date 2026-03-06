package com.example.mobilecomputing

import android.content.Context
import androidx.room.Database
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobilecomputing.DAO.CommentDAO
import com.example.mobilecomputing.DAO.PostDAO
import com.example.mobilecomputing.DAO.RelationshipDAO
import com.example.mobilecomputing.DAO.UserProfileDao
import com.example.mobilecomputing.DAO.PostReactionDAO
import com.example.mobilecomputing.entity.CommentEntity
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.PostReactionEntity
import com.example.mobilecomputing.entity.RelationshipEntity
import com.example.mobilecomputing.entity.UserProfileEntity

@Database(entities = [
    UserProfileEntity::class,
    PostEntity::class,
    RelationshipEntity::class,
    PostReactionEntity::class, CommentEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDAO(): UserProfileDao
    abstract  fun postDAO(): PostDAO
    abstract  fun relationDAO(): RelationshipDAO
    abstract  fun postReactionDAO(): PostReactionDAO
    abstract  fun commentDAO(): CommentDAO
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}