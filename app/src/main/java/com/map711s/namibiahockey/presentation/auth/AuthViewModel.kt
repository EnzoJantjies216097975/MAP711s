package com.map711s.namibiahockey.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.states.LoginState
import com.map711s.namibiahockey.data.states.RegisterState
import com.map711s.namibiahockey.data.states.UserProfileState
import com.map711s.namibiahockey.domain.usecase.auth.GetUserProfileUseCase
import com.map711s.namibiahockey.domain.usecase.auth.LoginUseCase
import com.map711s.namibiahockey.domain.usecase.auth.LogoutUseCase
import com.map711s.namibiahockey.domain.usecase.auth.RegisterUseCase
import com.map711s.namibiahockey.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    init {
        // Check if user is already logged in and load profile
        viewModelScope.launch {
            getUserProfileUseCase()
                .onSuccess { user ->
                    _userProfileState.update {
                        it.copy(
                            isLoading = false,
                            user = user
                        )
                    }
                    _loginState.update {
                        it.copy(
                            isLoggedIn = true,
                            userId = user.id
                        )
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        _loginState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            loginUseCase(email, password)
                .onSuccess { userId ->
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            userId = userId
                        )
                    }
                    loadUserProfile(userId)
                }
                .onFailure { exception ->
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Login failed"
                        )
                    }
                }
        }
    }

    // Implement other view model methods...
}

// Similarly update other ViewModels