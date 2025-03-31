package com.map711s.namibiahockey.data.remote

import com.map711s.namibiahockey.data.models.*
import retrofit2.http.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout()

    @POST("auth/password/reset")
    suspend fun resetPassword(@Body request: PasswordResetRequest)

    @POST("auth/password/change")
    suspend fun changePassword(@Body request: PasswordChangeRequest)
}