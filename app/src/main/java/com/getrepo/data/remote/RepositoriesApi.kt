package com.getrepo.data.remote

import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.data.remote.dto.RepositoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RepositoriesApi {

    @GET("search/repositories?q=language:kotlin&sort=stars")
    suspend fun getRepositories(
        @Query("page") page: Int,
        @Query("per_page") amountOfItems: Int = ITEMS_PER_PAGE,
    ): RepositoryResponse
}