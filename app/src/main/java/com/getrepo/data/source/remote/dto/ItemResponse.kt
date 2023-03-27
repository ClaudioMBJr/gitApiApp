package com.getrepo.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class ItemResponse(
    val id: Int,
    val name: String,
    val owner: OwnerResponse,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("forks_count")
    val forksCount: Int
)
