package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.models.User
import com.map711s.namibiahockey.data.repository.UserRepository
import com.map711s.namibiahockey.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState

    private val _registerState = MutableStateFlow<Resource<User>?>(null)
    val registerState: StateFlow<Resource<User>?> = _registerState

    private val _resetPasswordState = MutableStateFlow<Resource<Unit>?>(null)
    val resetPasswordState: StateFlow<Resource<Unit>?> = _resetPasswordState

    val isLoggedIn = userRepository.isLoggedIn()

    fun login(email: String, password: String){
        viewModelScope.launch {
            _loginState.value = Resource.Loading()

            val result = userRepository.login(email, password)
            _loginState.value = result
        }
    }

    fun register(name: String, email: String, password: String, phone: String? = null) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()

            val result = userRepository.register(name, email, password, phone)
            _registerState.value = result
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = Resource.Loading()

            val result = userRepository.resetPassword(email)
            _resetPasswordState.value = result
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()

            // Reset states
            _loginState.value = null
            _registerState.value = null
            _resetPasswordState.value = null
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }

    fun clearRegisterState() {
        _registerState.value = null
    }

    fun clearResetPasswordState() {
        _resetPasswordState.value = null
    }
}