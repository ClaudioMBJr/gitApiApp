package com.getrepo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OwnerResponse(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)
