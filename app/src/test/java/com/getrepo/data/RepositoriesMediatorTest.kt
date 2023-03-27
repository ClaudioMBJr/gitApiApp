package com.getrepo.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.getrepo.common.Constants.INITIAL_PAGE
import com.getrepo.common.Constants.ITEMS_PER_PAGE
import com.getrepo.data.source.RepositoriesMediator
import com.getrepo.data.source.local.RepositoriesDatabase
import com.getrepo.data.source.local.entity.RepositoryEntity
import com.getrepo.data.source.remote.RepositoriesApi
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@Config(maxSdk = 30)
class RepositoriesMediatorTest {

    private val api: RepositoriesApi = mockk()
    private lateinit var database: RepositoriesDatabase
    private lateinit var remoteMediator: RemoteMediator<Int, RepositoryEntity>

    private val maxOfItemsToEndOfPaginationReached = 4
    private val pagingState = PagingState<Int, RepositoryEntity>(
        listOf(),
        null,
        PagingConfig(1),
        0
    )

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RepositoriesDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        remoteMediator = RepositoriesMediator(api, database, maxOfItemsToEndOfPaginationReached)
    }

    @Test
    fun `WHEN api_getRepositories() throws an exception THEN remoteMediator must returns errorResult`() =
        runTest {
            coEvery { api.getRepositories(any(), any()) } throws Exception()

            val result = remoteMediator.load(LoadType.REFRESH, pagingState)

            assertThat(result).isInstanceOf(MediatorResult.Error::class.java)
        }

    @Test
    fun `WHEN loadType is a LoadType_REFRESH and has more pages THEN returns successResult and endOfPaginationReached equals false`() =
        runTest {
            coEvery { api.getRepositories(any(), any()) } returns getRepositoriesResponsePageOne

            val result = remoteMediator.load(LoadType.REFRESH, pagingState)

            coVerify { api.getRepositories(INITIAL_PAGE, ITEMS_PER_PAGE) }

            assertThat(result).isInstanceOf(MediatorResult.Success::class.java)
            assertThat((result as MediatorResult.Success).endOfPaginationReached).isFalse()
            assertEquals(database.repositoriesDao().selectAll(), repositoriesEntityPageOne)
            assertThat(database.repositoriesDao().selectAll().size)
                .isLessThan(maxOfItemsToEndOfPaginationReached)
        }

    @Test
    fun `WHEN loadType is a LoadType_REFRESH and hasn't more pages THEN returns successResult and endOfPaginationReached equals true`() =
        runTest {
            coEvery { api.getRepositories(any(), any()) } returns getRepositoriesResponseMax

            val result = remoteMediator.load(LoadType.REFRESH, pagingState)

            coVerify { api.getRepositories(INITIAL_PAGE, ITEMS_PER_PAGE) }

            assertThat(result).isInstanceOf(MediatorResult.Success::class.java)
            assertThat((result as MediatorResult.Success).endOfPaginationReached).isTrue()
            assertEquals(database.repositoriesDao().selectAll(), repositoriesEntityPageMax)
            assertThat(database.repositoriesDao().selectAll().size)
                .isEqualTo(maxOfItemsToEndOfPaginationReached)
        }

    @Test
    fun `WHEN loadType is a LoadType_REFRESH THEN database must be cleared`() =
        runTest {
            coEvery { api.getRepositories(any(), any()) } returns getRepositoriesResponsePageOne

            database.repositoriesDao().insertAll(repositoriesEntityPageTwo)
            assertThat(
                database.repositoriesDao().selectAll().containsAll(repositoriesEntityPageTwo)
            ).isTrue()

            val result = remoteMediator.load(LoadType.REFRESH, pagingState)

            assertThat(result).isInstanceOf(MediatorResult.Success::class.java)
            assertThat(database.repositoriesDao().selectAll().containsAll(repositoriesEntityPageTwo)).isFalse()
            assertEquals(database.repositoriesDao().selectAll(), repositoriesEntityPageOne)
            assertThat(database.repositoriesDao().selectAll().size)
                .isEqualTo(repositoriesEntityPageOne.size)
        }

    @Test
    fun `WHEN loadType is a LoadType_PREPEND THEN returns successResult and endOfPaginationReached equals true`() =
        runTest {
            val result = remoteMediator.load(LoadType.PREPEND, pagingState)

            coVerify { api.getRepositories(any(), any()) wasNot Called }

            assertThat(result).isInstanceOf(MediatorResult.Success::class.java)
            assertThat((result as MediatorResult.Success).endOfPaginationReached).isTrue()
        }

    @Test
    fun `WHEN loadType is a LoadType_APPEND THEN returns successResult and database must have the correct list`() =
        runTest {
            val nextPage = INITIAL_PAGE.plus(1)
            val pagingStateAppend = PagingState(
                listOf(PagingSource.LoadResult.Page(repositoriesEntityPageOne, INITIAL_PAGE, nextPage)),
                null,
                PagingConfig(1),
                0
            )

            coEvery { api.getRepositories(any(), any()) } returns getRepositoriesResponsePageTwo
            database.repositoriesDao().insertAll(repositoriesEntityPageOne)

            assertThat(database.repositoriesDao().selectAll().containsAll(repositoriesEntityPageOne)).isTrue()

            val result = remoteMediator.load(LoadType.APPEND, pagingStateAppend)

            coVerify { api.getRepositories(nextPage, ITEMS_PER_PAGE) }

            assertThat(result).isInstanceOf(MediatorResult.Success::class.java)
            assertThat(database.repositoriesDao().selectAll().containsAll(repositoriesEntityPageOne + repositoriesEntityPageOne )).isTrue()
            assertThat(database.repositoriesDao().selectAll().containsAll(repositoriesEntityPageOne + repositoriesEntityPageOne + repositoriesEntityPageMax)).isFalse()
        }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }
}