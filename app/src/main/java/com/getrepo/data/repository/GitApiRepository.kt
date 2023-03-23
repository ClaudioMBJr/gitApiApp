package com.getrepo.data.repository

import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.data.local.GitRepositoriesDao
import com.getrepo.data.local.entity.ItemEntity
import com.getrepo.data.mapper.toItemEntity
import com.getrepo.data.remote.GitApi
import javax.inject.Inject

class GitApiRepository @Inject constructor(
    private val gitAPI: GitApi,
    private val dao: GitRepositoriesDao
) {

    suspend fun getRepositories(page: Int): Result<List<ItemEntity>> {
        return try {
            val repositories = gitAPI.getRepositories(page)
                .items
                .map { it.toItemEntity() }

            dao.insertAll(repositories)

            Result.success(repositories)
        } catch (e: Exception) {
            val cachedResult = dao.getAll(offset = (page - 1) * ITEMS_PER_PAGE)

            if (cachedResult.isNotEmpty())
                Result.success(cachedResult)
            else
                Result.failure(e)
        }
    }
}