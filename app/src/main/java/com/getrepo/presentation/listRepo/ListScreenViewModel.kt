package com.getrepo.presentation.listRepo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import com.getrepo.domain.use_case.GetRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : ViewModel() {

    var listScreenState by mutableStateOf(ListScreenState())
        private set

    fun getRepositories() = getRepositoriesUseCase().cachedIn(viewModelScope)

    fun handleState(loadState: CombinedLoadStates) {
        listScreenState = listScreenState.copy(
            isLoadingFirstPage = loadState.refresh is LoadState.Loading,
            hasErrorWhileLoadingFirstPage = loadState.refresh is LoadState.Error,
            isLoadingMoreItems = loadState.append is LoadState.Loading,
            hasErrorWhileLoadingMoreItems = loadState.refresh is LoadState.Error
        )
    }

}