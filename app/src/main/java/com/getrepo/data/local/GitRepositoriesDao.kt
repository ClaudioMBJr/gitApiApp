package com.getrepo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.data.local.entity.ItemEntity

@Dao
interface GitRepositoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Query("SELECT * FROM repositories LIMIT :limit OFFSET :offset")
    fun getAll(limit: Int = ITEMS_PER_PAGE, offset: Int): List<ItemEntity>
}