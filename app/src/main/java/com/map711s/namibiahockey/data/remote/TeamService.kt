package com.map711s.namibiahockey.data.remote

import com.map711s.namibiahockey.data.models.*
import retrofit2.http.*
import java.io.File

interface TeamService {
    @GET("teams")
    suspend fun getAllTeams(): List<TeamSummary>

    @GET("teams/user")
    suspend fun getUserTeams(): List<TeamSummary>

    @GET("teams/{id}")
    suspend fun getTeamWithPlayers(@Path("id") teamId: String): TeamWithPlayers

    @GET("teams/{id}/stats")
    suspend fun getTeamStats(
        @Path("id") teamId: String,
        @Query("season") season: String
    ): TeamStats

    @GET("teams/{id}/matches")
    suspend fun getTeamMatchResults(@Path("id") teamId: String): List<TeamMatchResult>

    @POST("teams")
    suspend fun createTeam(@Body teamRequest: TeamRequest): Team

    @PUT("teams/{id}")
    suspend fun updateTeam(
        @Path("id") teamId: String,
        @Body teamRequest: TeamRequest
    ): Team

    @Multipart
    @POST("teams/{id}/logo")
    suspend fun uploadTeamLogo(
        @Path("id") teamId: String,
        @Part("logo") logoFile: File
    ): Team

    @POST("teams/players")
    suspend fun addPlayerToTeam(@Body teamPlayer: TeamPlayer)

    @DELETE("teams/{teamId}/players/{playerId}")
    suspend fun removePlayerFromTeam(
        @Path("teamId") teamId: String,
        @Path("playerId") playerId: String
    )

    @PUT("teams/players")
    suspend fun updateTeamPlayer(@Body teamPlayer: TeamPlayer)

    @GET("teams/division/{division}")
    suspend fun getTeamsByDivision(@Path("division") division: String): List<TeamSummary>

    @GET("teams/search")
    suspend fun searchTeams(@Query("query") query: String): List<TeamSummary>
}