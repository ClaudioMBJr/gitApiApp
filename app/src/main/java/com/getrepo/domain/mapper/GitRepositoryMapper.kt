package com.getrepo.domain.mapper

import com.getrepo.data.local.entity.ItemEntity
import com.getrepo.domain.model.GitRepository

fun ItemEntity.toGitRepository() =
    GitRepository(
        repositoryName = repositoryName,
        ownerName = ownerName,
        ownerAvatarUrl = ownerAvatarUrl,
        amountOfStars = amountOfStars,
        amountOfForks = amountOfForks
    )