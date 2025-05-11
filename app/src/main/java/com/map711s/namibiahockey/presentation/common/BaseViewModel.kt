package com.map711s.namibiahockey.presentation.common

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException


abstract class BaseViewModel<S> : ViewModel() {
    // Protected mutable state flow, only viewmodels can update
    protected val _state = MutableStateFlow<S>(createInitialState())

    // Public immutable stateflow for UI to observe
    val state: StateFlow<S> = _state.asStateFlow()

    // Each ViewModel subclass defines its initial state
    abstract fun createInitialState(): S

    // Common error handling function
    protected fun handleError(exception: Exception, errorHandler: (Exception) -> S): S {
        val errorMessage = when (exception) {
            is FirebaseAuthException -> mapFirebaseAuthError(exception)
            is FirebaseFirestoreException -> mapFirestoreError(exception)
            is IOException -> "Network error: Please check your connection"
            else -> exception.message ?: "An unexpected error occurred"
        }

        return errorHandler(exception)
    }

    private fun mapFirebaseAuthError(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email format"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_USER_NOT_FOUND" -> "Account doesn't exist"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already in use"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak"
            else -> exception.message ?: "Authentication error"
        }
    }

    private fun mapFirestoreError(exception: FirebaseFirestoreException): String {
        return when (exception.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE -> "Service unavailable, please try again later"
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You don't have permission to perform this action"
            else -> exception.message ?: "Data error"
        }
    }
}