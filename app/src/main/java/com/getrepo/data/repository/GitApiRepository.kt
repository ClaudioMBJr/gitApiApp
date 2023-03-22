package com.getrepo.data.repository

import com.getrepo.data.remote.GitApi
import com.getrepo.data.remote.dto.GitRespositoriesResponse
import javax.inject.Inject

class GitApiRepository @Inject constructor(private val gitAPI: GitApi) {

    suspend fun getRepositories(page: Int): Result<GitRespositoriesResponse> {
        return try {
            Result.success(gitAPI.getRepositories(page))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}