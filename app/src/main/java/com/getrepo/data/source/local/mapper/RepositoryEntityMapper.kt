package com.getrepo.data.source.local.mapper

import com.getrepo.data.source.local.entity.RepositoryEntity
import com.getrepo.data.source.remote.dto.ItemResponse

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