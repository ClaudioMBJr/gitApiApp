package com.getrepo.data.mapper

import com.getrepo.data.local.entity.RepositoryEntity
import com.getrepo.data.remote.dto.ItemResponse

fun ItemResponse.toRepositoryEntity() =
    RepositoryEntity(
        repositoryId = id,
        repositoryName = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        amountOfStars = stargazersCount,
        amountOfForks = forksCount,
        page = 1
    )