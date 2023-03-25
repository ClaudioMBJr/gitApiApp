package com.getrepo.domain.di

import com.getrepo.data.local.RepositoriesDatabase
import com.getrepo.data.remote.RepositoriesApi
import com.getrepo.domain.use_case.GetRepositoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object GetRepoDomainDI {

    @ViewModelScoped
    @Provides
    fun providesGetRepositoriesUseCase(gitApi: RepositoriesApi, database: RepositoriesDatabase) =
        GetRepositoriesUseCase(gitApi, database)
}