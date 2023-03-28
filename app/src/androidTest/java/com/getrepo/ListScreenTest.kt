package com.getrepo

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.getrepo.data.source.RepositoriesMediator
import com.getrepo.data.source.local.RepositoriesDatabase
import com.getrepo.data.source.local.entity.RepositoryEntity
import com.getrepo.data.source.remote.RepositoriesApi
import com.getrepo.data.source.remote.dto.ItemResponse
import com.getrepo.data.source.remote.dto.OwnerResponse
import com.getrepo.data.source.remote.dto.RepositoryResponse
import com.getrepo.data.source.repository.RepositoriesRepository
import com.getrepo.domain.use_case.GetRepositoriesUseCase
import com.getrepo.presentation.MainActivity
import com.getrepo.presentation.listRepo.ListScreen
import com.getrepo.presentation.listRepo.ListScreenViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalPagingApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@HiltAndroidTest
class ListScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val api: RepositoriesApi = mockk()
    private lateinit var database: RepositoriesDatabase
    private lateinit var remoteMediator: RemoteMediator<Int, RepositoryEntity>
    private lateinit var repositoriesRepository: RepositoriesRepository
    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase
    private lateinit var listScreenViewModel: ListScreenViewModel

    @Before
    fun setup() = runTest {
        coEvery { api.getRepositories(any(), any()) } throws Exception()

        database = Room.inMemoryDatabaseBuilder(
            context,
            RepositoriesDatabase::class.java
        ).allowMainThreadQueries().build()

        remoteMediator = RepositoriesMediator(api, database, 60)
        repositoriesRepository = RepositoriesRepository(remoteMediator, database)
        getRepositoriesUseCase = GetRepositoriesUseCase(repositoriesRepository)
        listScreenViewModel = ListScreenViewModel(getRepositoriesUseCase)
    }

    @Test
    fun listScreenTest() {
        val pager = mutableListOf<ItemResponse>()
        for (i in 1..40) {
            pager.add(ItemResponse(i, "", OwnerResponse("", ""), 1, 1))
        }
        val getRepositoriesResponse = RepositoryResponse(1, false, pager)

        composeRule.activity.setContent {
            ListScreen(listScreenViewModel)
        }

        composeRule.onNodeWithTag("errorFirstPage").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.text_error_loading_first_page))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.text_btn_try_again))
            .assertIsDisplayed()

        coEvery { api.getRepositories(1, any()) } returns getRepositoriesResponse
        coEvery { api.getRepositories(more(1), any()) } throws Exception()

        composeRule.onNodeWithText(context.getString(R.string.text_btn_try_again))
            .assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("errorFirstPage").assertDoesNotExist()
        composeRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
        composeRule.onNodeWithTag("itemId${20}")
        composeRule.onNodeWithTag("lazyColumn").performScrollToIndex(19)
        composeRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
        composeRule.onNodeWithTag("itemId${40}")
        composeRule.onNodeWithTag("lazyColumn").performScrollToIndex(39)

        composeRule.onNodeWithTag("errorWithItems").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.text_error_loading_more_items))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.text_btn_try_again))
            .assertIsDisplayed()


        val lastPage = mutableListOf<ItemResponse>()
        for (i in 41..60) {
            pager.add(ItemResponse(i, "", OwnerResponse("", ""), 1, 1))
        }
        val getRepositoriesLastResponse = RepositoryResponse(1, false, lastPage)

        coEvery { api.getRepositories(any(), any()) } returns getRepositoriesLastResponse

        composeRule.onNodeWithText(context.getString(R.string.text_btn_try_again))
            .performClick()

        composeRule.onNodeWithTag("progressWithItems").assertDoesNotExist()
        composeRule.onNodeWithTag("errorWithItems").assertDoesNotExist()

        composeRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
        composeRule.onNodeWithTag("itemId${60}")
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }
}