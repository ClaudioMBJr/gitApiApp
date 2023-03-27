package com.getrepo.data.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.room.Room
import com.getrepo.common.Constants.BASE_URL
import com.getrepo.common.Constants.DATABASE_NAME
import com.getrepo.data.source.RepositoriesMediator
import com.getrepo.data.source.local.RepositoriesDatabase
import com.getrepo.data.source.local.entity.RepositoryEntity
import com.getrepo.data.source.remote.RepositoriesApi
import com.getrepo.data.source.repository.RepositoriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
@OptIn(ExperimentalPagingApi::class)
object GetRepoDataDI {

    @Provides
    @Singleton
    fun providesRepositoriesApi(): RepositoriesApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    .build()
            ).build().create(RepositoriesApi::class.java)

    @Provides
    @Singleton
    fun createDB(
        @ApplicationContext context: Context
    ): RepositoriesDatabase =
        Room.databaseBuilder(context, RepositoriesDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun provideRepositoriesMediator(
        api: RepositoriesApi,
        database: RepositoriesDatabase,
    ): RemoteMediator<Int, RepositoryEntity> =
        RepositoriesMediator(api, database)

    @Provides
    @Singleton
    fun providesRepositoriesRepository(
        repositoryMediator: RemoteMediator<Int, RepositoryEntity>,
        database: RepositoriesDatabase
    ): RepositoriesRepository =
        RepositoriesRepository(repositoryMediator, database)

}