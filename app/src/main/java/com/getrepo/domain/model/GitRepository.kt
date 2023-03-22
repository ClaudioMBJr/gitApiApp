package com.getrepo.domain.model

data class GitRepository(
    val repositoryName: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val amountOfStars: Int,
    val amountOfForks: Int
)
