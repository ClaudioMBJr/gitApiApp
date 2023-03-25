package com.getrepo.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.getrepo.data.local.entity.RepositoryEntity

@Dao
interface RepositoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RepositoryEntity>)

    @Query("SELECT * FROM repository ORDER BY page")
    fun getAll(): PagingSource<Int, RepositoryEntity>

    @Query("SELECT COUNT(repository_id) FROM repository")
    fun getAmountOfItems(): Int

    @Query("DELETE FROM repository")
    fun clearAll()
}