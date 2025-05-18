package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.domain.model.UserRole
import com.map711s.namibiahockey.domain.repository.AuthRepository

class RegisterUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        phone: String,
        role: UserRole = UserRole.PLAYER
    ): Result<String> {
        // Validate inputs
        if (email.isBlank() || password.isBlank() || name.isBlank() || phone.isBlank()) {
            return Result.failure(IllegalArgumentException("All fields are required"))
        }

        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match"))
        }

        // Simple email validation
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        // Password strength check
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        return authRepository.registerUser(email, password, name, phone, role)
    }
}