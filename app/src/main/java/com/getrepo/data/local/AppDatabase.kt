package com.getrepo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.getrepo.data.local.entity.ItemEntity

@Database(entities = [ItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gitRepositoriesDao(): GitRepositoriesDao
}