package com.example.mobilecomputing

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobilecomputing.DAO.PostDAO
import com.example.mobilecomputing.DAO.UserProfileDao
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.UserProfileEntity

@Database(entities = [UserProfileEntity::class, PostEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDAO(): UserProfileDao
    abstract  fun postDAO(): PostDAO
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