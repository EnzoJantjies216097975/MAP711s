package com.map711s.namibiahockey.viewmodel

import com.map711s.namibiahockey.data.states.LoginState
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.data.states.RegisterState
import com.map711s.namibiahockey.data.states.UserProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.UserRole
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

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

    // Login function
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginUiState.Error("Email and password cannot be empty")
            return
        }

        _state.value = LoginUiState.Loading

        viewModelScope.launch {
            authRepository.loginUser(email, password)
                .onSuccess { firebaseUser ->
                    _state.value = LoginUiState.Success(firebaseUser.uid)
                    loadUserProfile()
                }
                .onFailure { exception ->
                    _state.value = LoginUiState.Error(
                        handleError(exception as Exception) {
                            LoginUiState.Error(it.message ?: "Login failed")
                        }.message
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
            _State.update {
                it.copy(
                    isLoading = false,
                    error = "Passwords do not match"
                )
            }
            return
        }

        _registerState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            authRepository.registerUser(email, password, name, phone, role)
                .onSuccess { firebaseUser ->
                    _registerState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            userId = firebaseUser.uid
                        )
                    }
                    _loginState.update {
                        it.copy(
                            isLoggedIn = true,
                            userId = firebaseUser.uid
                        )
                    }
                    loadUserProfile()
                }
                .onFailure { exception ->
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