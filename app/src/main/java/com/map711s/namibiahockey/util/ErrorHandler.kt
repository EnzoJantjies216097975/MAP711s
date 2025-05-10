package com.map711s.namibiahockey.util

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Error
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.time.Duration

object ErrorHandler {
    // Show toast error message
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(context, message, duration).show()
    }

    // Show snackbar error message
    fun showSnackbar (snackbarHostState: SnackbarHostState, message: String, actionLabel: String? = null): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel
            )
        }
    }

    // Ma common errors to user-frienndly messages
    fun mapErrorMessage(error: Throwable): String {
        return when (error) {
            is FirebaseAuthException -> mapFirebaseAuthError(error)
            is FirebaseFirestoreException -> mapFirestoreError(error)
            is ConnectException -> "No internet connection"
            is SocketTimeoutException -> "Connection timed out"
            else -> error.message ?: "An unexpected erroro occured"
        }
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
            else -> exception.message ?: "Database error"
        }
    }
}