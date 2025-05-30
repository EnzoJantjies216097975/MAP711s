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

// UI state data class
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

    private val _uiState = MutableStateFlow(PlayerRequestUiState())
    val uiState: StateFlow<PlayerRequestUiState> = _uiState.asStateFlow()

    init {
        loadPendingRequests()
    }

    fun sendJoinRequest(teamId: String, message: String = "") {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: return@launch

            // FIXED: Handle Result type properly and provide explicit types
            authRepository.getUserProfile(currentUserId)
                .onSuccess { userProfile ->
                    val request = PlayerRequest(
                        playerId = currentUserId,
                        playerName = userProfile.name,
                        teamId = teamId,
                        requestType = RequestType.JOIN,
                        requestedBy = currentUserId,
                        message = message
                    )

                    // FIXED: Use correct method name and handle Result
                    playerRequestRepository.createRequest(request)
                        .onSuccess {
                            _uiState.update { state ->
                                state.copy(showSuccessMessage = "Join request sent!")
                            }
                        }
                        .onFailure { exception ->
                            _uiState.update { state ->
                                state.copy(errorMessage = exception.message)
                            }
                        }
                }
                .onFailure { exception ->
                    _uiState.update { state ->
                        state.copy(errorMessage = "Failed to get user profile: ${exception.message}")
                    }
                }
        }
    }

    fun respondToRequest(requestId: String, approved: Boolean) {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: return@launch

            // FIXED: Handle Result type properly
            playerRequestRepository.respondToRequest(requestId, approved, currentUserId)
                .onSuccess {
                    loadPendingRequests()
                    _uiState.update { state ->
                        state.copy(
                            showSuccessMessage = if (approved) "Request approved!" else "Request rejected!"
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update { state ->
                        state.copy(errorMessage = exception.message)
                    }
                }
        }
    }

    private fun loadPendingRequests() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true)
            }

            // This would need to be implemented based on your app's requirements
            // For now, just update loading state
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    requests = emptyList() // Placeholder
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { state ->
            state.copy(
                errorMessage = null,
                showSuccessMessage = null
            )
        }
    }
}