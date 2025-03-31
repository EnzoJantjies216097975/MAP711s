package com.map711s.namibiahockey.data.remote

import com.map711s.namibiahockey.data.models.*
import retrofit2.http.*

interface UserService {
    @GET("user/profile")
    suspend fun getUserProfile(): UserProfile

    @PUT("user/profile")
    suspend fun updateProfile(@Body user: User): User

    @GET("user/teams")
    suspend fun getUserTeams(): List<UserTeam>
}