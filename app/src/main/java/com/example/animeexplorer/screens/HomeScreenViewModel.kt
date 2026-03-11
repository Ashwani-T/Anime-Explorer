package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.domain.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.http.Query

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    private var searchJob: Job? = null

    private var currentPage: Int = 1

    init {
        observeQuery()
        loadInitialPage()
    }

    private suspend fun loadAnimePage(
        query: String,
        page: Int,
        append: Boolean
    ){
        _uiState.update {it.copy(isLoading = true)}

        val result = repository.getAnimeList(query= query, page = page)
        Log.d("ViewModel", "loadAnimePage: ${result.pagination}")

        _uiState.update { state ->
            val merged = (state.animeList + result.data)
            val newList = merged.distinctBy { it.id }
            state.copy(
                animeList = newList,
                isLoading = false,
                endReached = !(result.pagination.hasNextPage)
            )
        }
        currentPage = result.pagination.currentPage
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery(){
        viewModelScope.launch {
            queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collectLatest { query ->
                    currentPage = 1
                    _uiState.update {
                        it.copy(
                            query = query,
                            animeList = emptyList(),
                            endReached = false
                        )
                    }
                    loadAnimePage(
                        query = query,
                        page = currentPage,
                        append = false
                    )
                }
        }
    }

    fun loadInitialPage(){
        searchJob = viewModelScope.launch {
            loadAnimePage(
                query = uiState.value.query,
                page = currentPage,
                append = false
            )
        }
    }

    fun onQueryChange(newQuery: String){
        queryFlow.value = newQuery
    }

    fun loadingNextPage(){
        val state = _uiState.value
        Log.d("ViewModel", "loadingNextPage: ${state.query} ")

        if(state.isLoading || state.endReached) return

        searchJob = viewModelScope.launch {
            val nextPage = currentPage + 1

            loadAnimePage(
                query = state.query,
                page = nextPage,
                append = true
            )
        }
    }

    fun cancelLoading(){
        if(searchJob?.isActive == true) {
            searchJob!!.cancel()
            Log.d("ViewModel", "cancelLoading ")
        }
    }


}