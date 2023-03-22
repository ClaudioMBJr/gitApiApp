package com.getrepo.data.remote

import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.data.remote.dto.GitRespositoriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitApi {

    @GET("search/repositories?q=language:kotlin&sort=stars")
    suspend fun getRepositories(
        @Query("page") page: Int,
        @Query("per_page") amountOfItems: Int = ITEMS_PER_PAGE,
    ): GitRespositoriesResponse
}