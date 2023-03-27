package com.getrepo.domain.use_case

import androidx.paging.map
import com.getrepo.data.source.repository.RepositoriesRepository
import com.getrepo.domain.mapper.toRepository
import kotlinx.coroutines.flow.map

class GetRepositoriesUseCase(
    private val repositoriesRepository: RepositoriesRepository
) {

    operator fun invoke() =
        repositoriesRepository.getRepositories()
            .map { pagingData -> pagingData.map { it.toRepository() } }


}