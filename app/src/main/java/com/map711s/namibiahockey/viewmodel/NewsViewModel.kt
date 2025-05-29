package com.map711s.namibiahockey.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.NewsRepository
import com.map711s.namibiahockey.data.states.ImageUploadState
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
    private val authRepository: AuthRepository,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    companion object {
        private const val TAG = "NewsViewModel"
        private const val NEWS_IMAGES_PATH = "news_images"
    }

    // News piece state
    private val _newsState = MutableStateFlow(NewsState())
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()

    // News piece list state
    private val _newsListState = MutableStateFlow(NewsListState())
    val newsListState: StateFlow<NewsListState> = _newsListState.asStateFlow()

    // Image upload state
    private val _imageUploadState = MutableStateFlow(ImageUploadState())
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()


    init {
        loadAllNewsPieces()
    }

    /**
     * Upload news image to Firebase Storage
     */
    fun uploadNewsImage(
        uri: Uri,
        onProgress: (Float) -> Unit,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Update upload state
                _imageUploadState.update {
                    it.copy(isUploading = true, progress = 0f, error = null)
                }

                // Get current user ID for file organization
                val userId = authRepository.getCurrentUserId() ?: "anonymous"

                // Create unique filename with timestamp and UUID
                val timestamp = System.currentTimeMillis()
                val uniqueId = UUID.randomUUID().toString().substring(0, 8)
                val fileName = "news_${userId}_${timestamp}_${uniqueId}.jpg"

                // Create storage reference
                val storageRef = firebaseStorage.reference
                val imageRef = storageRef.child("$NEWS_IMAGES_PATH/$fileName")

                Log.d(TAG, "Starting upload for: $fileName")

                // Start upload task
                val uploadTask = imageRef.putFile(uri)

                // Monitor upload progress
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat() / 100f

                    Log.d(TAG, "Upload progress: ${(progress * 100).toInt()}%")

                    // Update progress in state and callback
                    _imageUploadState.update { it.copy(progress = progress) }
                    onProgress(progress)
                }

                // Handle upload completion
                uploadTask.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Upload completed successfully")

                        // Get download URL
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val downloadUrl = downloadUri.toString()
                            Log.d(TAG, "Download URL obtained: $downloadUrl")

                            // Update state and notify success
                            _imageUploadState.update {
                                it.copy(
                                    isUploading = false,
                                    isSuccess = true,
                                    downloadUrl = downloadUrl,
                                    progress = 1.0f
                                )
                            }
                            onSuccess(downloadUrl)

                        }.addOnFailureListener { exception ->
                            Log.e(TAG, "Failed to get download URL", exception)
                            val error = Exception("Failed to get download URL: ${exception.message}")

                            _imageUploadState.update {
                                it.copy(
                                    isUploading = false,
                                    error = error.message,
                                    progress = 0f
                                )
                            }
                            onFailure(error)
                        }
                    } else {
                        val exception = task.exception ?: Exception("Upload failed")
                        Log.e(TAG, "Upload failed", exception)

                        _imageUploadState.update {
                            it.copy(
                                isUploading = false,
                                error = exception.message,
                                progress = 0f
                            )
                        }
                        onFailure(exception)
                    }
                }

                // Handle upload failure
                uploadTask.addOnFailureListener { exception ->
                    Log.e(TAG, "Upload task failed", exception)

                    val errorMessage = when (exception) {
                        is StorageException -> {
                            when (exception.errorCode) {
                                StorageException.ERROR_OBJECT_NOT_FOUND -> "File not found"
                                StorageException.ERROR_BUCKET_NOT_FOUND -> "Storage bucket not found"
                                StorageException.ERROR_PROJECT_NOT_FOUND -> "Project not found"
                                StorageException.ERROR_QUOTA_EXCEEDED -> "Storage quota exceeded"
                                StorageException.ERROR_NOT_AUTHENTICATED -> "Authentication required"
                                StorageException.ERROR_NOT_AUTHORIZED -> "Unauthorized access"
                                StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> "Upload timeout"
                                else -> "Storage error: ${exception.message}"
                            }
                        }
                        else -> "Upload failed: ${exception.message}"
                    }

                    _imageUploadState.update {
                        it.copy(
                            isUploading = false,
                            error = errorMessage,
                            progress = 0f
                        )
                    }
                    onFailure(Exception(errorMessage))
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error starting upload", e)

                _imageUploadState.update {
                    it.copy(
                        isUploading = false,
                        error = "Failed to start upload: ${e.message}",
                        progress = 0f
                    )
                }
                onFailure(e)
            }
        }
    }

    /**
     * Delete image from Firebase Storage
     */
    fun deleteNewsImage(imageUrl: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                // Extract file path from URL
                val fileRef = firebaseStorage.getReferenceFromUrl(imageUrl)

                Log.d(TAG, "Deleting image: ${fileRef.path}")

                fileRef.delete().await()
                Log.d(TAG, "Image deleted successfully")
                onComplete(true)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete image", e)
                onComplete(false)
            }
        }
    }

    /**
     * Reset image upload state
     */
    fun resetImageUploadState() {
        _imageUploadState.update { ImageUploadState() }
    }

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
    fun deleteNewsPiece(newsId: String, imageUrl: String? = null) {
        _newsState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // First delete the associated image if it exists
            if (!imageUrl.isNullOrEmpty()) {
                try {
                    deleteNewsImage(imageUrl)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to delete associated image, continuing with news deletion", e)
                }
            }

            // Then delete the news piece
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
                    Log.e(TAG, "Error loading news pieces: ${exception.message}", exception)
                }
        }
    }

    fun resetNewsState() {
        _newsState.update { NewsState() }
    }

    // Load news pieces by hockey type - Fixed: removed suspend keyword
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

    fun toggleBookmark(newsId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            try {
                // Get the current news piece
                newsRepository.getNewsPiece(newsId)
                    .onSuccess { newsPiece ->
                        // Update the bookmark status
                        val updatedNews = newsPiece.copy(isBookmarked = isBookmarked)

                        // Save the updated news piece
                        newsRepository.updateNewsPiece(updatedNews)
                            .onSuccess {
                                // Update the local state
                                _newsState.update { it.copy(newsPiece = updatedNews) }

                                // Update the news in the list state
                                val updatedList = _newsListState.value.newsPieces.map { news ->
                                    if (news.id == newsId) {
                                        news.copy(isBookmarked = isBookmarked)
                                    } else {
                                        news
                                    }
                                }
                                _newsListState.update { it.copy(newsPieces = updatedList) }

                                Log.d(TAG, "Bookmark toggled for news: $newsId, bookmarked: $isBookmarked")
                            }
                            .onFailure { exception ->
                                Log.e(TAG, "Failed to toggle bookmark", exception)
                                _newsState.update {
                                    it.copy(error = "Failed to update bookmark: ${exception.message}")
                                }
                            }
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to get news piece for bookmark toggle", exception)
                        _newsState.update {
                            it.copy(error = "Failed to toggle bookmark: ${exception.message}")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error in toggleBookmark", e)
                _newsState.update {
                    it.copy(error = "Failed to toggle bookmark: ${e.message}")
                }
            }
        }
    }

    fun loadBookmarkedNews() {
        _newsListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            newsRepository.getBookmarkedNewsPieces()
                .onSuccess { bookmarkedNews ->
                    _newsListState.update {
                        it.copy(
                            isLoading = false,
                            newsPieces = bookmarkedNews
                        )
                    }
                    Log.d(TAG, "Loaded ${bookmarkedNews.size} bookmarked news pieces")
                }
                .onFailure { exception ->
                    _newsListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load bookmarked news"
                        )
                    }
                    Log.e(TAG, "Error loading bookmarked news: ${exception.message}", exception)
                }
        }
    }
}