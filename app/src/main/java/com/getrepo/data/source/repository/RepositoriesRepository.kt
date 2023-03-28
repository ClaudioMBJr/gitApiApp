package com.getrepo.data.source.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.RemoteMediator
import com.getrepo.common.Constants
import com.getrepo.data.source.local.RepositoriesDatabase
import com.getrepo.data.source.local.entity.RepositoryEntity

@OptIn(ExperimentalPagingApi::class)
class RepositoriesRepository(
    private val repositoriesMediator: RemoteMediator<Int, RepositoryEntity>,
    private val database: RepositoriesDatabase
) {

    fun getRepositories() =
        Pager(
            config = PagingConfig(
                pageSize = Constants.ITEMS_PER_PAGE,
                initialLoadSize = Constants.ITEMS_PER_PAGE,
            ),
            pagingSourceFactory = {
                database.repositoriesDao().getAll()
            },
            remoteMediator = repositoriesMediator
        ).flow
}