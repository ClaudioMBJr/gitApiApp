package com.getrepo.domain.use_case

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.data.local.RepositoriesDatabase
import com.getrepo.data.remote.RepositoriesApi
import com.getrepo.data.source.RepositoriesDataSource
import com.getrepo.domain.mapper.toGitRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class GetRepositoriesUseCase @Inject constructor(
    private val gitApi: RepositoriesApi,
    private val database: RepositoriesDatabase,
) {

    operator fun invoke() =
        Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE, // How many items you want to load initially
            ),
            pagingSourceFactory = {
                database.repositoriesDao().getAll()
            },
            remoteMediator = RepositoriesDataSource(
                gitApi,
                database,
            )
        ).flow
            .map { pagingData -> pagingData.map {it.toGitRepository()} }

}