package com.getrepo.presentation.listRepo

import androidx.paging.PagingData
import com.getrepo.data.source.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class ListScreenState(
    val isLoadingFirstPage: Boolean = false,
    val hasErrorWhileLoadingFirstPage: Boolean = false,
    val isLoadingMoreItems: Boolean = false,
    val hasErrorWhileLoadingMoreItems: Boolean = false,
    val flow : Flow<PagingData<RepositoryEntity>> = flowOf()
)
