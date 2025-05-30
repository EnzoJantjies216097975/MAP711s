package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.RequestType
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.PlayerRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// FIXED: Add missing UI state data class
data class PlayerRequestUiState(
    val isLoading: Boolean = false,
    val requests: List<PlayerRequest> = emptyList(),
    val errorMessage: String? = null,
    val showSuccessMessage: String? = null
)

@HiltViewModel
class PlayerRequestViewModel @Inject constructor(
    private val playerRequestRepository: PlayerRequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // FIXED: Add missing _uiState property
    private val _uiState = MutableStateFlow(PlayerRequestUiState())
    val uiState: StateFlow<PlayerRequestUiState> = _uiState.asStateFlow()

    init {
        loadPendingRequests()
    }

    fun sendJoinRequest(teamId: String, message: String = "") {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: return@launch
            val userProfile = authRepository.getUserProfile(currentUserId).getOrNull() ?: return@launch

            val request = PlayerRequest(
                playerId = currentUserId,
                playerName = userProfile.name,
                teamId = teamId,
                requestType = RequestType.JOIN,
                requestedBy = currentUserId,
                message = message
            )

            playerRequestRepository.createRequest(request)
                .onSuccess {
                    _uiState.update { it.copy(showSuccessMessage = "Join request sent!") }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(errorMessage = exception.message) }
                }
        }
    }

    fun respondToRequest(requestId: String, approved: Boolean) {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: return@launch

            playerRequestRepository.respondToRequest(requestId, approved, currentUserId)
                .onSuccess {
                    loadPendingRequests()
                    _uiState.update {
                        it.copy(showSuccessMessage = if (approved) "Request approved!" else "Request rejected!")
                    }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(errorMessage = exception.message) }
                }
        }
    }

    // FIXED: Add missing loadPendingRequests method
    private fun loadPendingRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // This would need to be implemented in the repository
            // For now, just update loading state
            _uiState.update {
                it.copy(
                    isLoading = false,
                    requests = emptyList() // Placeholder
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                showSuccessMessage = null
            )
        }
    }
}