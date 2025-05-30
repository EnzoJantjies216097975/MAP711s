package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.RequestType
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.PlayerRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerRequestViewModel @Inject constructor(
    private val playerRequestRepository: PlayerRequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

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
}