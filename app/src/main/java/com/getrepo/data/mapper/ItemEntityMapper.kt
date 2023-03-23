package com.getrepo.data.mapper

import com.getrepo.data.local.entity.ItemEntity
import com.getrepo.data.remote.dto.ItemResponse

fun ItemResponse.toItemEntity() =
    ItemEntity(
        repositoryName = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        amountOfStars = stargazersCount,
        amountOfForks = forksCount
    )