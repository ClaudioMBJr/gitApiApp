package com.getrepo.domain

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import app.cash.turbine.test
import com.getrepo.CoroutinesTestRule
import com.getrepo.data.repositoriesEntityPageOne
import com.getrepo.data.source.repository.RepositoriesRepository
import com.getrepo.domain.mapper.toRepository
import com.getrepo.domain.model.Repository
import com.getrepo.domain.use_case.GetRepositoriesUseCase
import com.getrepo.utils.PagerListCallback
import com.getrepo.utils.TestDiffCallback
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GetRepositoriesTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private val repository: RepositoriesRepository = mockk(relaxed = true)
    private val useCase = GetRepositoriesUseCase(repository)

    @Test
    fun `WHEN calling getRepositories THEN it must call repository_getRepositories()`() =
        runTest {
            coEvery { repository.getRepositories() } returns flowOf(
                PagingData.from(repositoriesEntityPageOne)
            )

            useCase()
            coVerify { repository.getRepositories() }
        }

    @Test
    fun `WHEN getRepositories result is success THEN it should return mappedRepositoryEntity`() =
        runTest {
            val differ = AsyncPagingDataDiffer<Repository>(
                diffCallback = TestDiffCallback(),
                updateCallback = PagerListCallback(),
                mainDispatcher = coroutinesTestRule.standardTestDispatcher,
            )

            coEvery { repository.getRepositories() } returns flowOf(
                PagingData.from(repositoriesEntityPageOne)
            )

            useCase().test {
                differ.submitData(awaitItem())
                assertEquals(
                    differ.snapshot().items,
                    repositoriesEntityPageOne.map { it.toRepository() })
                cancelAndIgnoreRemainingEvents()
            }
        }
}