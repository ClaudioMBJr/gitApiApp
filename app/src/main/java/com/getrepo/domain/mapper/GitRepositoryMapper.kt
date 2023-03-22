package com.getrepo.domain.mapper

import com.getrepo.data.remote.dto.ItemsResponse
import com.getrepo.domain.model.GitRepository

fun ItemsResponse.toGitRepository() =
    GitRepository(
        repositoryName = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        amountOfStars = stargazersCount,
        amountOfForks = forksCount
    )