package com.getrepo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitRespositoriesResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<ItemsResponse>
)
