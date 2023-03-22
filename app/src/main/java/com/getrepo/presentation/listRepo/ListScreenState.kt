package com.getrepo.presentation.listRepo

import com.getrepo.domain.model.GitRepository

data class ListScreenState(
    val items: List<GitRepository> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMoreItems: Boolean = false,
    val hasError: Boolean = false
)
