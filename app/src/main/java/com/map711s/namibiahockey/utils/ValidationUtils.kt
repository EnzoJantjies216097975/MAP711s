package com.map711s.namibiahockey.utils

import android.util.Patterns

object ValidationUtils {

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            name.length > 50 -> "Name must be less than 50 characters"
            !name.matches(Regex("^[a-zA-Z\\s'-]+$")) -> "Name contains invalid characters"
            else -> null
        }
    }

    fun validatePhone(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone number is required"
            phone.length < 10 -> "Phone number must be at least 10 digits"
            !phone.matches(Regex("^[+]?[0-9\\s()-]+$")) -> "Invalid phone number format"
            else -> null
        }
    }

    fun validateJerseyNumber(number: String): String? {
        return when {
            number.isBlank() -> "Jersey number is required"
            number.toIntOrNull() == null -> "Jersey number must be a number"
            number.toInt() < 1 || number.toInt() > 99 -> "Jersey number must be between 1 and 99"
            else -> null
        }
    }

    fun validateAge(age: String): String? {
        return when {
            age.isBlank() -> "Age is required"
            age.toIntOrNull() == null -> "Age must be a number"
            age.toInt() < 16 || age.toInt() > 50 -> "Age must be between 16 and 50"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}