package com.getrepo.domain.mapper

import com.getrepo.data.source.local.entity.RepositoryEntity
import com.getrepo.domain.model.Repository

fun RepositoryEntity.toRepository() =
    Repository(
        repositoryName = repositoryName,
        ownerName = ownerName,
        ownerAvatarUrl = ownerAvatarUrl,
        amountOfStars = amountOfStars,
        amountOfForks = amountOfForks
    )