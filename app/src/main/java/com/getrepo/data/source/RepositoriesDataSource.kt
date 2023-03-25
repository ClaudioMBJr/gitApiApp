package com.getrepo.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.getrepo.common.Constants.INITIAL_PAGE
import com.getrepo.common.Constants.LIMIT_OF_ITEMS
import com.getrepo.data.local.RepositoriesDatabase
import com.getrepo.data.local.entity.RepositoryEntity
import com.getrepo.data.mapper.toRepositoryEntity
import com.getrepo.data.remote.RepositoriesApi
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RepositoriesDataSource @Inject constructor(
    private val repository: RepositoriesApi,
    private val database: RepositoriesDatabase
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
            val result = repository.getRepositories(page)
            val repositoriesEntity = result.items.map { it.toRepositoryEntity() }
            var endOfPaginationReached = false

            database.withTransaction {
                val repositoriesDao = database.repositoriesDao()

                if (loadType == LoadType.REFRESH)
                    repositoriesDao.clearAll()

                repositoriesDao.insertAll(repositoriesEntity.onEach { it.page = page })

                endOfPaginationReached = repositoriesDao.getAmountOfItems() == LIMIT_OF_ITEMS
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}