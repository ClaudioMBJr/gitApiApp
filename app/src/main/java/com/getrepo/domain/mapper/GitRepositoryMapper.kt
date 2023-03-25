package com.getrepo.domain.mapper

import com.getrepo.data.local.entity.RepositoryEntity
import com.getrepo.domain.model.GitRepository

fun RepositoryEntity.toGitRepository() =
    GitRepository(
        repositoryName = repositoryName,
        ownerName = ownerName,
        ownerAvatarUrl = ownerAvatarUrl,
        amountOfStars = amountOfStars,
        amountOfForks = amountOfForks
    )