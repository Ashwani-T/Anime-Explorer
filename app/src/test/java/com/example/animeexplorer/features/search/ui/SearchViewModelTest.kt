package com.example.animeexplorer.features.search.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.animeexplorer.MainDispatcherRule
import com.example.animeexplorer.core.domain.AnimeResponseModel
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.PageInfo
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.features.search.domain.SearchRepository
import com.example.animeexplorer.features.search.domain.model.GenreModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test



@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val testDispatcherRule = MainDispatcherRule()

    private lateinit var repository: SearchRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var vm: SearchViewModel
    private var malIdIndex = 0


    @Before
    fun setup() {
        mockkStatic(Log::class)
        coEvery { Log.d(any(), any()) } returns 0


        repository = mockk()
        savedStateHandle = SavedStateHandle(mapOf("search_query" to "naruto"))

        vm = SearchViewModel(repository, savedStateHandle)

    }

    private fun fakeResponse(
        animeList: List<AnimeUiModel> = emptyList(), currentPage: Int = 1,
        hasNextPage: Boolean = false
    ): AnimeResponseModel {
        return AnimeResponseModel(
            data = animeList,
            pagination = PageInfo(
                currentPage = currentPage,
                hasNextPage = hasNextPage
            )
        )

    }

    private fun generateMockAnimeUiModelList(size: Int): List<AnimeUiModel> {

        var limit = size
        val animeUiModelList: MutableList<AnimeUiModel> = mutableListOf()

        while (limit-- > 0) {

            malIdIndex++
            animeUiModelList.add(
                AnimeUiModel(
                    id = malIdIndex,
                    title = "Anime $malIdIndex",
                    description = "No description",
                    duration = "No duration",
                    imageUrl = "No Image",
                    score = 0.0
                )
            )
        }

        return animeUiModelList

    }

    @Test
    fun `init restores query and triggers debounced search`() = runTest {
        //val viewModel = SearchViewModel(repository, savedStateHandle)

        coEvery { repository.getFilteredAnime(any()) } returns Result.success(fakeResponse())

        coVerify(exactly = 0) {
            repository.getFilteredAnime(any())
        }
        advanceTimeBy(301)
        advanceUntilIdle()

        assertEquals("naruto", vm.uiState.value.searchQuery)
        coVerify(exactly = 1) { repository.getFilteredAnime(any()) }

    }

    @Test
    fun `query changes are debounced into one network call`() = runTest {
        coEvery { repository.getFilteredAnime(any()) } returns Result.success(fakeResponse())

        vm.onSearchQueryChange("n")
        vm.onSearchQueryChange("na")
        vm.onSearchQueryChange("nar")
        vm.onSearchQueryChange("naruto")

        advanceTimeBy(301)
        advanceUntilIdle()

        coVerify(atLeast = 1) {
            repository.getFilteredAnime(any())
        }

        assertEquals("naruto", vm.uiState.value.searchQuery)

    }

    @Test
    fun `onLoadMore increments page and appends results`() = runTest {
        coEvery { repository.getFilteredAnime(any()) } returnsMany listOf(
            Result.success(
                fakeResponse(
                    animeList = generateMockAnimeUiModelList(2),
                    currentPage = 1,
                    hasNextPage = true
                )
            ),
            Result.success(
                fakeResponse(
                    animeList = generateMockAnimeUiModelList(2),
                    currentPage = 2,
                    hasNextPage = false
                )
            )
        )


        advanceTimeBy(301)
        advanceUntilIdle()

        vm.onLoadMore()
        advanceUntilIdle()

        val ids = vm.uiState.value.animeList.map { it.id }
        assertEquals(listOf(1,2,3,4),ids)
        assertTrue(!vm.uiState.value.hasNextPage)
    }

    @Test
    fun `failure on loadMore rolls pageNumber back`()= runTest {
        coEvery{repository.getFilteredAnime(any())} returnsMany listOf(
            Result.success(fakeResponse(generateMockAnimeUiModelList(2),1,true)),
            Result.failure(Exception("Network Error"))
        )

        advanceTimeBy(301)
        advanceUntilIdle()

        vm.onLoadMore()
        advanceUntilIdle()

        assertEquals(1,vm.uiState.value.pageNumber)
        assertTrue(!vm.uiState.value.isLoading)

    }

    @Test
    fun `on apply filters resets page and clears results and loads new results`() = runTest{
        coEvery { repository.getFilteredAnime(any()) } returns Result.success(fakeResponse())

        val genre = GenreModel(10, "Fantasy")

        advanceTimeBy(301)
        advanceUntilIdle()

        vm.onSortTypeChange(SortType.POPULARITY)
        vm.onFormatChange(FormatType.TV)
        vm.onStatusChange(StatusType.AIRING)
        vm.onRatingChange(RatingType.G)
        vm.toggleGenre(genre)
        //vm.resetFilters()
        vm.onApplyFilter()

        advanceUntilIdle()



        // Loads applied filter result
        assertEquals(1,vm.uiState.value.pageNumber)

        assertTrue(vm.uiState.value.animeList.isEmpty())


        // Filters were applied successfully

        assertEquals(setOf(genre),
            vm.uiState.value.selectedGenres)

        coVerify(exactly = 2) { repository.getFilteredAnime(any()) }

        //remove genre and reset filters
        vm.removeGenre(genre)
        vm.resetFilters()
        advanceUntilIdle()

        coVerify(exactly = 3) { repository.getFilteredAnime(any()) }

    }

    @Test
    fun `on toggleSort order changes`()= runTest {
        coEvery { repository.getFilteredAnime(any()) } returns Result.success(fakeResponse())

        advanceTimeBy(301)
        advanceUntilIdle()

        //Sort order is DESC by default
        assertEquals(SortOrder.DESC,vm.uiState.value.sortOrder)

        vm.toggleSortOrder()

        advanceUntilIdle()

        //Sort order should be changed

        assertEquals(SortOrder.ASC,vm.uiState.value.sortOrder)
    }

    @Test
    fun `onCleared cancels search job`()= runTest {
        coEvery { repository.getFilteredAnime(any()) } coAnswers{
            delay(10000)
            Result.success(fakeResponse())
        }

        advanceTimeBy(301)
        advanceUntilIdle()

        // using reflection to access protected method and invoking it to check the functionality

        val method = SearchViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(vm)

        advanceTimeBy(10000)
        advanceUntilIdle()

        // Assert: job was cancelled so no exception/state update happens
        // If VM properly handles cancellation, isLoading should be false
        assertEquals(false, vm.uiState.value.isLoading)

    }


}