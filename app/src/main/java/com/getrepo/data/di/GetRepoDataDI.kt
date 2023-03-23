package com.getrepo.data.di

import android.content.Context
import androidx.room.Room
import com.getrepo.common.Constants.BASE_URL
import com.getrepo.data.local.AppDatabase
import com.getrepo.data.local.GitRepositoriesDao
import com.getrepo.data.remote.GitApi
import com.getrepo.data.repository.GitApiRepository
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
object GetRepoDataDI {

    @Provides
    @Singleton
    fun providesGitApi(): GitApi =
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
            ).build().create(GitApi::class.java)

    @Provides
    @Singleton
    fun providesGitApiRepository(gitAPI: GitApi, gitRepositoriesDao: GitRepositoriesDao): GitApiRepository =
        GitApiRepository(gitAPI, gitRepositoriesDao)


    @Provides
    @Singleton
    fun createDB(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "database")
        .build()

    @Provides
    @Singleton
    fun providesGitRepositoriesDao(appDatabase: AppDatabase): GitRepositoriesDao =
        appDatabase.gitRepositoriesDao()
}