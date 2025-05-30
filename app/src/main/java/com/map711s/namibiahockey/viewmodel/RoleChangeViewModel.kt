package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.RoleChangeRequest
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.RoleChangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleChangeViewModel @Inject constructor(
    private val roleChangeRepository: RoleChangeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoleChangeUiState())
    val uiState: StateFlow<RoleChangeUiState> = _uiState.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<RoleChangeRequest>>(emptyList())
    val pendingRequests: StateFlow<List<RoleChangeRequest>> = _pendingRequests.asStateFlow()

    private val _userRequests = MutableStateFlow<List<RoleChangeRequest>>(emptyList())
    val userRequests: StateFlow<List<RoleChangeRequest>> = _userRequests.asStateFlow()

    private val _pendingRequestsCount = MutableStateFlow(0)
    val pendingRequestsCount: StateFlow<Int> = _pendingRequestsCount.asStateFlow()

    init {
        observePendingRequestsCount()
    }

    fun requestRoleChange(
        requestedRole: UserRole,
        reason: String
    ) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            val userProfile = authRepository.getUserProfile(currentUserId).getOrNull()
            if (userProfile == null) {
                _uiState.update { it.copy(isLoading = false, error = "User profile not found") }
                return@launch
            }

            if (userProfile.role == requestedRole) {
                _uiState.update { it.copy(isLoading = false, error = "You already have this role") }
                return@launch
            }

            val request = RoleChangeRequest(
                userId = currentUserId,
                userName = userProfile.name,
                userEmail = userProfile.email,
                currentRole = userProfile.role,
                requestedRole = requestedRole,
                reason = reason.trim()
            )

            roleChangeRepository.createRoleChangeRequest(request)
                .onSuccess { requestId ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Role change request submitted successfully"
                        )
                    }
                    loadUserRequests(currentUserId)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to submit role change request"
                        )
                    }
                }
        }
    }

    fun loadPendingRequests() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            roleChangeRepository.getPendingRequests()
                .onSuccess { requests ->
                    _pendingRequests.value = requests
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load pending requests"
                        )
                    }
                }
        }
    }

    fun loadUserRequests(userId: String? = null) {
        val targetUserId = userId ?: authRepository.getCurrentUserId()
        if (targetUserId == null) {
            _uiState.update { it.copy(error = "Not authenticated") }
            return
        }

        viewModelScope.launch {
            roleChangeRepository.getUserRoleChangeRequests(targetUserId)
                .onSuccess { requests ->
                    _userRequests.value = requests
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            error = exception.message ?: "Failed to load user requests"
                        )
                    }
                }
        }
    }

    fun approveRoleChangeRequest(requestId: String, adminResponse: String = "") {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            // Find the request to get user details
            val request = _pendingRequests.value.find { it.id == requestId }
            if (request == null) {
                _uiState.update { it.copy(isLoading = false, error = "Request not found") }
                return@launch
            }

            // First approve the request
            roleChangeRepository.approveRoleChangeRequest(requestId, currentUserId, adminResponse)
                .onSuccess {
                    // Then update the user's role
                    updateUserRole(request.userId, request.requestedRole)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to approve request"
                        )
                    }
                }
        }
    }

    fun rejectRoleChangeRequest(requestId: String, adminResponse: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            roleChangeRepository.rejectRoleChangeRequest(requestId, currentUserId, adminResponse)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Request rejected successfully"
                        )
                    }
                    loadPendingRequests()
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to reject request"
                        )
                    }
                }
        }
    }

    private suspend fun updateUserRole(userId: String, newRole: UserRole) {
        authRepository.getUserProfile(userId)
            .onSuccess { user ->
                val updatedUser = user.copy(role = newRole)
                authRepository.updateUserProfile(updatedUser)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                successMessage = "Role change approved and user updated successfully"
                            )
                        }
                        loadPendingRequests()
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Request approved but failed to update user role: ${exception.message}"
                            )
                        }
                    }
            }
            .onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Request approved but failed to get user profile: ${exception.message}"
                    )
                }
            }
    }

    private fun observePendingRequestsCount() {
        viewModelScope.launch {
            roleChangeRepository.observePendingRequestsCount()
                .collect { count ->
                    _pendingRequestsCount.value = count
                }
        }
    }

    fun resetUiState() {
        _uiState.update { RoleChangeUiState() }
    }
}

data class RoleChangeUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)