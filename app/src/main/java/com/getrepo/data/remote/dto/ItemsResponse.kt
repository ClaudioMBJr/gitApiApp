package com.getrepo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ItemsResponse(
    val name: String,
    val owner: OwnerResponse,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("forks_count")
    val forksCount: Int
)
