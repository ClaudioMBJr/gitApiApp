package com.getrepo.domain.use_case

import com.getrepo.data.repository.GitApiRepository
import com.getrepo.domain.mapper.toGitRepository
import javax.inject.Inject

class GetRepositoriesUseCase @Inject constructor(private val gitApiRepository: GitApiRepository) {

    suspend operator fun invoke(page: Int) =
        gitApiRepository.getRepositories(page)
            .getOrThrow().map { it.toGitRepository() }

}