package com.map711s.namibiahockey.data.remote

import com.map711s.namibiahockey.data.models.*
import retrofit2.http.*
import java.io.File

interface EventService {
    @GET("events")
    suspend fun getAllEvents(): List<EventListItem>

    @GET("events/upcoming")
    suspend fun getUpcomingEvents(): List<EventListItem>

    @GET("events/past")
    suspend fun getPastEvents(): List<EventListItem>

    @GET("events/user")
    suspend fun getUserEvents(): List<EventListItem>

    @GET("events/{id}")
    suspend fun getEventWithTeams(@Path("id") eventId: String): EventWithTeams

    @GET("events/{id}/matches")
    suspend fun getEventMatches(@Path("id") eventId: String): List<MatchSummary>

    @POST("events")
    suspend fun createEvent(@Body eventRequest: EventRequest): Event

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") eventId: String,
        @Body eventRequest: EventRequest
    ): Event

    @Multipart
    @POST("events/{id}/image")
    suspend fun uploadEventImage(
        @Path("id") eventId: String,
        @Part("image") imageFile: File
    ): Event

    @POST("events/register")
    suspend fun registerTeamForEvent(@Body request: EventRegistrationRequest): EventRegistration

    @DELETE("events/{eventId}/registrations/{teamId}")
    suspend fun cancelEventRegistration(
        @Path("eventId") eventId: String,
        @Path("teamId") teamId: String
    )

    @POST("events/matches")
    suspend fun createMatch(@Body matchRequest: MatchRequest): Match

    @PUT("events/matches/{id}/result")
    suspend fun updateMatchResult(
        @Path("id") matchId: String,
        @Body resultRequest: MatchResultRequest
    ): Match

    @GET("events/type/{type}")
    suspend fun getEventsByType(@Path("type") type: EventType): List<EventListItem>

    @GET("events/search")
    suspend fun searchEvents(@Query("query") query: String): List<EventListItem>

    @GET("events/date")
    suspend fun getEventsByDateRange(
        @Query("start") startDate: Long,
        @Query("end") endDate: Long
    ): List<EventListItem>
}