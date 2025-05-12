package com.map711s.namibiahockey.presentation.news


import com.map711s.namibiahockey.presentation.news.state.NewsListState
import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.presentation.news.state.NewsState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    // News piece state
    private val _newsState = MutableStateFlow(NewsState())
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()

    // News piece list state
    private val _newsListState = MutableStateFlow(NewsListState())
    val newsListState: StateFlow<NewsListState> = _newsListState.asStateFlow()

    // Create a new news piece
    fun createNewsPiece(newsPiece: NewsPiece) {
        _newsState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = newsRepository.createNewsPiece(newsPiece)
            if (result.isSuccess) {
                val newsId = result.getOrNull() // or however you access the value
                _newsState.update { it.copy(isLoading = false, newsPiece = newsPiece.copy(id = newsId)) }
                loadAllNewsPieces()
            } else {
                val exception = result.exceptionOrNull() // or however you access the error
                _newsState.update {
                    it.copy(
                        isLoading = false,
                        error = exception?.message ?: "Failed to create news piece"
                    )
                }
            }
        }
    }

    // Get a news piece by ID
    fun getNewsPiece(newsId: String) {
        _newsState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.getNewsPiece(newsId)
                .onSuccess { newsPiece ->
                    _newsState.update { it.copy(isLoading = false, newsPiece = newsPiece) }
                }
                .onFailure { exception ->
                    _newsState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to get news piece") }
                }
        }
    }

    // Update an existing news piece
    fun updateNewsPiece(newsPiece: NewsPiece) {
        _newsState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.updateNewsPiece(newsPiece)
                .onSuccess {
                    _newsState.update { it.copy(isLoading = false, newsPiece = newsPiece) }
                    loadAllNewsPieces()
                }
                .onFailure { exception ->
                    _newsState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to update news piece") }
                }
        }
    }

    // Delete a news piece by ID
    fun deleteNewsPiece(newsId: String) {
        _newsState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.deleteNewsPiece(newsId)
                .onSuccess {
                    _newsState.update { it.copy(isLoading = false) }
                    loadAllNewsPieces()
                }
                .onFailure { exception ->
                    _newsState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to delete news piece") }
                }
        }
    }

    // Load all news pieces
    fun loadAllNewsPieces() {
        _newsListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.getAllNewsPieces()
                .onSuccess { newsPieces ->
                    _newsListState.update { it.copy(isLoading = false, newsPieces = newsPieces) }
                }
                .onFailure { exception ->
                    _newsListState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to load news pieces") }
                    Log.e("NewsViewModel", "Error loading news pieces: ${exception.message}", exception)
                }
        }
    }

    fun resetNewsState() {
        _newsState.update { NewsState() }
    }
}
