package com.map711s.namibiahockey.data.remote

import com.map711s.namibiahockey.data.models.*
import retrofit2.http.*
import java.io.File

interface PlayerService {
    @GET("players")
    suspend fun getAllPlayers(): List<PlayerListItem>

    @GET("teams/{id}/players")
    suspend fun getTeamPlayers(@Path("id") teamId: String): List<PlayerListItem>

    @GET("players/{id}")
    suspend fun getPlayerWithDetails(@Path("id") playerId: String): PlayerWithDetails

    @GET("players/{id}/stats")
    suspend fun getPlayerStats(
        @Path("id") playerId: String,
        @Query("season") season: String
    ): PlayerStats

    @GET("players/{id}/performances")
    suspend fun getPlayerMatchPerformances(@Path("id") playerId: String): List<PlayerMatchPerformance>

    @POST("players")
    suspend fun createPlayer(@Body playerRequest: PlayerRequest): Player

    @PUT("players/{id}")
    suspend fun updatePlayer(
        @Path("id") playerId: String,
        @Body playerRequest: PlayerRequest
    ): Player

    @Multipart
    @POST("players/{id}/photo")
    suspend fun uploadPlayerPhoto(
        @Path("id") playerId: String,
        @Part("photo") photoFile: File
    ): Player

    @PUT("players/{id}/link/{userId}")
    suspend fun linkPlayerToUser(
        @Path("id") playerId: String,
        @Path("userId") userId: String
    )

    @GET("players/position/{position}")
    suspend fun getPlayersByPosition(@Path("position") position: String): List<PlayerListItem>

    @GET("players/search")
    suspend fun searchPlayers(@Query("query") query: String): List<PlayerListItem>

    @GET("players/user/teams")
    suspend fun getUserTeamPlayers(): List<PlayerListItem>
}