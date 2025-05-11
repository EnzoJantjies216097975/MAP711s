package com.map711s.namibiahockey.presentation.auth.state

/**
 * Represents all possible UI states for the login screen.
 * Using a sealed class pattern allows for exhaustive when expressions
 * and provides type safety when handling different states.
 */
sealed class LoginUiState {
    /**
     * Initial state before any user interaction
     */
    object Initial : LoginUiState()

    /**
     * Loading state while authentication is in progress
     */
    object Loading : LoginUiState()

    /**
     * Success state after successful authentication
     * @param userId The ID of the authenticated user
     * @param isNewUser Whether this is a new user (useful for onboarding flows)
     */
    data class Success(
        val userId: String,
        val isNewUser: Boolean = false
    ) : LoginUiState()

    /**
     * Error state when authentication fails
     * @param message Error message to display to the user
     * @param cause Type of error that occurred (useful for specific error handling)
     */
    data class Error(
        val message: String,
        val cause: ErrorCause = ErrorCause.UNKNOWN
    ) : LoginUiState()

    /**
     * Categorizes different types of login errors for more specific error handling
     */
    enum class ErrorCause {
        INVALID_CREDENTIALS,
        NETWORK_ERROR,
        USER_NOT_FOUND,
        TOO_MANY_ATTEMPTS,
        UNKNOWN
    }

    /**
     * Helper extension to check if the state is loading
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Helper extension to check if the state represents an error
     */
    val isError: Boolean get() = this is Error

    /**
     * Helper extension to check if the state represents success
     */
    val isSuccess: Boolean get() = this is Success
}