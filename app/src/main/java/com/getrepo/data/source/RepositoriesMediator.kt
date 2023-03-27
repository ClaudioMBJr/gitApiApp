package com.getrepo.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.getrepo.common.Constants.INITIAL_PAGE
import com.getrepo.common.Constants.LIMIT_OF_ITEMS
import com.getrepo.data.source.local.RepositoriesDatabase
import com.getrepo.data.source.local.entity.RepositoryEntity
import com.getrepo.data.source.local.mapper.toRepositoryEntity
import com.getrepo.data.source.remote.RepositoriesApi

@OptIn(ExperimentalPagingApi::class)
class RepositoriesMediator(
    private val api: RepositoriesApi,
    private val database: RepositoriesDatabase,
    private val maxOfItems: Int = LIMIT_OF_ITEMS
) : RemoteMediator<Int, RepositoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepositoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val currentPage = state.pages.lastOrNull()?.data?.lastOrNull()?.page

                val nextPage = currentPage?.plus(INITIAL_PAGE)
                nextPage
                    ?: return MediatorResult.Success(endOfPaginationReached = currentPage != null)
            }
        }

        return try {
            val result = api.getRepositories(page)
            val repositoriesEntity = result.items.map { it.toRepositoryEntity() }
            var endOfPaginationReached = false

            database.withTransaction {
                val repositoriesDao = database.repositoriesDao()

                if (loadType == LoadType.REFRESH)
                    repositoriesDao.clearAll()

                repositoriesDao.insertAll(repositoriesEntity.onEach { it.page = page })

                endOfPaginationReached = repositoriesDao.getAmountOfItems() >= maxOfItems
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}