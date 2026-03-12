package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.data.ConnectivityObserver
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
    private val repository: AnimeRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val isConnected = MutableStateFlow(false)

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        observeNetworkStatus()
        observeQuery()
        loadInitialPage()

    }


    private fun loadInitialPage() {
        viewModelScope.launch {
            if (isConnected.value) {
                loadAnimePage(query = "", append = false)
            }
        }
    }

    private suspend fun loadAnimePage(query: String, append: Boolean) {

        val targetPage = if (append) currentPage + 1 else 1

        _uiState.update { it.copy(isLoading = true) }

        try {
            val result = repository.getAnimeList(query, targetPage)

            _uiState.update { state ->
                val newList = if (append) {
                    (state.animeList + result.data).distinctBy { it.id }
                } else {
                    result.data
                }

                state.copy(
                    animeList = newList,
                    endReached = result.pagination.hasNextPage.not(),
                    isLoading = false
                )
            }

            currentPage = targetPage
            Log.d("viewModel", ":${result.pagination} $currentPage")

        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .collectLatest { q ->

                    // Update UI state immediately
                    _uiState.update {
                        it.copy(
                            query = q,
                            animeList = emptyList(),
                            endReached = false
                        )
                    }
                    currentPage = 1


                    loadAnimePage(query = q, append = false)

                }
        }
    }

    fun onQueryChange(newQuery: String) {
        queryFlow.value = newQuery
    }

    fun loadNextPage() {
        if (!isConnected.value) return
        if (_uiState.value.endReached) return
        if (_uiState.value.isLoading) return

        searchJob = viewModelScope.launch {
            loadAnimePage(_uiState.value.query, append = true)
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityObserver.observer()
                .distinctUntilChanged()
                .collectLatest { status ->
                    isConnected.value = status

                    if (status) {
                        // Net regained
                        val state = _uiState.value
                        val q = queryFlow.value
                        when{
                            state.isLoading -> Unit
                            state.animeList.isEmpty() ->{
                                loadAnimePage(query = q, append = false)
                            }
                            !state.endReached -> {
                                loadAnimePage(q,true)
                            }
                        }
                    }else{
                        cancelLoading()
                    }
                }
        }
    }

    fun cancelLoading(){
        if(searchJob?.isActive == true) searchJob!!.cancel()
    }
}