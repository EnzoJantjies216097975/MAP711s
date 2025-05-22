package com.map711s.namibiahockey.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.data.repository.NewsRepository
import com.map711s.namibiahockey.data.states.NewsListState
import com.map711s.namibiahockey.data.states.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val storage: FirebaseStorage
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
            newsRepository.createNewsPiece(newsPiece)
                .onSuccess { newsId ->
                    _newsState.update { it.copy(isLoading = false, newsPiece = newsPiece.copy(id = newsId)) }
                    loadAllNewsPieces() // Refresh the list after creation
                }
                .onFailure { exception ->
                    _newsState.update { it.copy(isLoading = false, error = exception.message ?: "Failed to create news piece") }
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

    // Toggle bookmark status for a news piece
    fun toggleBookmark(newsId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            // Get the current news piece
            val currentNewsPiece = _newsState.value.newsPiece ?: return@launch

            // Create updated news piece with new bookmark status
            val updatedNewsPiece = currentNewsPiece.copy(isBookmarked = isBookmarked)

            // Update in repository
            newsRepository.updateNewsPiece(updatedNewsPiece)
                .onSuccess {
                    // Update local state
                    _newsState.update { it.copy(newsPiece = updatedNewsPiece) }

                    // Also update in the list if present
                    _newsListState.update { state ->
                        val updatedList = state.newsPieces.map { news ->
                            if (news.id == newsId) news.copy(isBookmarked = isBookmarked) else news
                        }
                        state.copy(newsPieces = updatedList)
                    }
                }
                .onFailure { exception ->
                    Log.e("NewsViewModel", "Error toggling bookmark: ${exception.message}")
                    // Optionally show error in UI, but we'll keep it silent for a smoother UX
                }
        }
    }

    // Upload image to Firebase Storage
    fun uploadNewsImage(
        imageUri: Uri,
        onProgress: (Float) -> Unit,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create a unique filename for the image
                val filename = "news_images/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(filename)

                // Create upload task
                val uploadTask = storageRef.putFile(imageUri)

                // Monitor for progress updates
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
                    onProgress(progress)
                }

                // Wait for upload to complete
                uploadTask.await()

                // Get download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()
                onSuccess(downloadUrl)

            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error uploading image: ${e.message}", e)
                onFailure(e)
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

    // Load news pieces by hockey type
    fun loadNewsPiecesByType(hockeyType: HockeyType) {
        _newsListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.getNewsPiecesByType(hockeyType)
                .onSuccess { newsPieces ->
                    _newsListState.update {
                        it.copy(
                            isLoading = false,
                            newsPieces = newsPieces
                        )
                    }
                }
                .onFailure { exception ->
                    _newsListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load news pieces"
                        )
                    }
                }
        }
    }

    // Load bookmarked news pieces
    fun loadBookmarkedNews() {
        _newsListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.getBookmarkedNewsPieces()
                .onSuccess { newsPieces ->
                    _newsListState.update {
                        it.copy(
                            isLoading = false,
                            newsPieces = newsPieces
                        )
                    }
                }
                .onFailure { exception ->
                    _newsListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load bookmarked news"
                        )
                    }
                }
        }
    }
}