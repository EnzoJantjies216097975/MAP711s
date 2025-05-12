package com.map711s.namibiahockey.presentation.auth

import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.mapper.toData
import com.map711s.namibiahockey.domain.model.UserRole
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.presentation.auth.state.LoginState
import com.map711s.namibiahockey.presentation.auth.state.LoginUiState
import com.map711s.namibiahockey.presentation.auth.state.RegisterState
import com.map711s.namibiahockey.presentation.common.BaseViewModel
import com.map711s.namibiahockey.presentation.profile.state.UserProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginUiState>() {

    override fun createInitialState(): LoginUiState = LoginUiState.Initial

    // Login state
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Registration state
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // User profile state
    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    init {
        // Check if user is already logged in
        if (authRepository.isUserLoggedIn()) {
            loadUserProfile()
        }
    }

    private fun updateUserProfile(domainUser: com.map711s.namibiahockey.domain.model.User) {
        viewModelScope.launch {
            // Convert domain model to data model
            val dataUser = com.map711s.namibiahockey.data.model.User(
                id = domainUser.id,
                email = domainUser.email,
                name = domainUser.name,
                phone = domainUser.phone,
                role = converttoDataRole(domainUser.role)
            )

            authRepository.updateUserProfile(dataUser)
        }
    }

    private fun converttoDataRole(domainRole: com.map711s.namibiahockey.domain.model.UserRole): com.map711s.namibiahockey.data.model.UserRole {
        return when(domainRole) {
            com.map711s.namibiahockey.domain.model.UserRole.ADMIN -> com.map711s.namibiahockey.data.model.UserRole.ADMIN
            com.map711s.namibiahockey.domain.model.UserRole.COACH -> com.map711s.namibiahockey.data.model.UserRole.COACH
            com.map711s.namibiahockey.domain.model.UserRole.MANAGER -> com.map711s.namibiahockey.data.model.UserRole.MANAGER
            com.map711s.namibiahockey.domain.model.UserRole.PLAYER -> com.map711s.namibiahockey.data.model.UserRole.PLAYER
        }
    }

    // Login function
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginUiState.Error(
                message = "Email and password cannot be empty"
            )
            return
        }

        _state.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val result = authRepository.loginUser(email, password)
                val userId = result.getOrThrow()
                _state.value = LoginUiState.Success(userId)
                loadUserProfile()
            } catch (exception: Exception) {
                _state.value = LoginUiState.Error(
                    message = exception.message ?: "Login failed",
                    cause = LoginUiState.ErrorCause.UNKNOWN
                )
            }
        }
    }

    // Register function
    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        phone: String,
        role: UserRole = UserRole.PLAYER
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() ||
            name.isBlank() || phone.isBlank()) {
            _registerState.update {
                it.copy(
                    isLoading = false,
                    error = "All fields are required"
                )
            }
            return
        }

        if (password != confirmPassword) {
            _registerState.update {
                it.copy(
                    isLoading = false,
                    error = "Passwords do not match"
                )
            }
            return
        }

        _registerState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val userId = authRepository.registerUser(
                    email, password, name, phone, role
                ).getOrThrow()

                _registerState.update {
                    it.copy(
                        isLoading = false,
                        isRegistered = true,
                        userId = userId
                    )
                }

                _loginState.update {
                    it.copy(
                        isLoggedIn = true,
                        userId = userId
                    )
                }

                loadUserProfile()
            } catch (exception: Exception) {
                _registerState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    // Logout function
    fun logout() {
        viewModelScope.launch {
            authRepository.logoutUser()
                .onSuccess {
                    // Reset all states
                    _loginState.update { LoginState() }
                    _userProfileState.update { UserProfileState() }
                }
        }
    }

    // Load user profile
    private fun loadUserProfile() {
        val userId = authRepository.getCurrentUserId() ?: return

        _userProfileState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            authRepository.getUserProfile(userId)
                .onSuccess { user ->
                    _userProfileState.update {
                        it.copy(
                            isLoading = false,
                            user = user
                        )
                    }
                }

                .onFailure { exception ->
                    _userProfileState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load user profile"
                        )
                    }
                }
        }
    }

    // Reset login form
    fun resetLoginState(){
        _loginState.update { LoginState(isLoggedIn = authRepository.isUserLoggedIn()) }
    }

    // Reset registration form
    fun resetRegisterState(){
        _registerState.update { RegisterState() }
    }

    // Password reset
    fun resetPassword(email: String){
        if (email.isBlank()) {
            _loginState.update {
                it.copy(
                    error = "Email cannot be empty"
                )
            }
            return
        }

        _loginState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            authRepository.resetPassword(email)
                .onSuccess {
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            message = "Password reset email sent. Check you inbox"
                        )
                    }
                }
                .onFailure { exception ->
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to send reset email"
                        )
                    }
                }
        }
    }
}