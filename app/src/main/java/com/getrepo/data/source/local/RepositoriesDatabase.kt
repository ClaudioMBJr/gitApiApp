package com.getrepo.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.getrepo.data.source.local.entity.RepositoryEntity

@Database(entities = [RepositoryEntity::class], version = 1)
abstract class RepositoriesDatabase : RoomDatabase() {
    abstract fun repositoriesDao(): RepositoriesDao
}