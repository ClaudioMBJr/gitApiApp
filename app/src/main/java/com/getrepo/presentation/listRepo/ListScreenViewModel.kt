package com.getrepo.presentation.listRepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.getrepo.domain.use_case.GetRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : ViewModel() {

    fun getRepositories() = getRepositoriesUseCase().cachedIn(viewModelScope)
}