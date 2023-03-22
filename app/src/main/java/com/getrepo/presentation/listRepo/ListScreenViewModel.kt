package com.getrepo.presentation.listRepo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.domain.di.IoDispatcher
import com.getrepo.domain.model.GitRepository
import com.getrepo.domain.use_case.GetRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    var listScreenState by mutableStateOf(ListScreenState())
        private set

    private val repoList = mutableListOf<GitRepository>()

    var page = 1
    var hasMoreItems = true
        private set

    init {
        getRepositories()
    }

    private fun getRepositories() {
        viewModelScope.launch(coroutineDispatcher) {
            listScreenState = try {
                handleResult(getRepositoriesUseCase(page))

                listScreenState.copy(
                    isLoading = false,
                    isLoadingMoreItems = false,
                    items = repoList.toList()
                )
            } catch (e: Exception) {
                listScreenState.copy(
                    hasError = true,
                    isLoading = false,
                    isLoadingMoreItems = false
                )
            }
        }
    }

    private fun handleResult(repositories: List<GitRepository>) {
        if (repositories.size < ITEMS_PER_PAGE)
            hasMoreItems = false
        else
            page++

        repoList.addAll(repositories)
    }

    fun getMoreItems() {
        listScreenState = listScreenState.copy(isLoadingMoreItems = page > 1)
        getRepositories()
    }

    fun canRequestMoreItems() =
        hasMoreItems && !listScreenState.isLoadingMoreItems && !listScreenState.hasError
}