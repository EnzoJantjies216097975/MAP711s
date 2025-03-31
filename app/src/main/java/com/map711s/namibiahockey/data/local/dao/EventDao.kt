package com.map711s.namibiahockey.data.local.dao

import androidx.room.*
import com.map711s.namibiahockey.data.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Event-related operations in the database.
 * This interface defines methods to interact with event data stored locally.
 */
@Dao
interface EventDao {

    /**
     * Insert an event into the database, replacing any existing event with the same ID.
     * @param event The event entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    /**
     * Insert multiple events into the database.
     * @param events The list of event entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    /**
     * Update an existing event in the database.
     * @param event The event entity with updated data.
     */
    @Update
    suspend fun updateEvent(event: Event)

    /**
     * Delete an event from the database.
     * @param event The event entity to delete.
     */
    @Delete
    suspend fun deleteEvent(event: Event)

    /**
     * Get an event by its ID.
     * @param eventId The unique identifier of the event.
     * @return The event entity or null if not found.
     */
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEvent(eventId: String): Event?

    /**
     * Get an event by its ID, returning as a Flow to observe changes.
     * @param eventId The unique identifier of the event.
     * @return A Flow emitting the event entity whenever it changes.
     */
    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventFlow(eventId: String): Flow<Event?>

    /**
     * Get all event list items for displaying in UI.
     * @return A list of event list items.
     */
    @Query("SELECT id, title, description, type, startDate, endDate, location, isRegistrationOpen, 0 as teamCount, imageUrl, status, 0 as isUserRegistered FROM events")
    suspend fun getAllEventListItems(): List<EventListItem>

    /**
     * Get upcoming event list items (where start date is in the future).
     * @return A list of upcoming event list items.
     */
    @Query("SELECT id, title, description, type, startDate, endDate, location, isRegistrationOpen, 0 as teamCount, imageUrl, status, 0 as isUserRegistered FROM events WHERE startDate > :currentTime")
    suspend fun getUpcomingEventListItems(currentTime: Long = System.currentTimeMillis()): List<EventListItem>

    /**
     * Get past event list items (where end date is in the past).
     * @return A list of past event list items.
     */
    @Query("SELECT id, title, description, type, startDate, endDate, location, isRegistrationOpen, 0 as teamCount, imageUrl, status, 0 as isUserRegistered FROM events WHERE endDate < :currentTime")
    suspend fun getPastEventListItems(currentTime: Long = System.currentTimeMillis()): List<EventListItem>

    /**
     * Get event list items for a specific user (events they're registered for).
     * @param userId The unique identifier of the user.
     * @return A list of event list items for the user.
     */
    @Query("SELECT e.id, e.title, e.description, e.type, e.startDate, e.endDate, e.location, e.isRegistrationOpen, 0 as teamCount, e.imageUrl, e.status, 1 as isUserRegistered " +
            "FROM events e " +
            "JOIN event_registrations er ON e.id = er.eventId " +
            "JOIN team_players tp ON er.teamId = tp.teamId " +
            "JOIN players p ON tp.playerId = p.id " +
            "WHERE p.userId = :userId")
    suspend fun getUserEventListItems(userId: String): List<EventListItem>

    /**
     * Get an event with its registered teams.
     * @param eventId The unique identifier of the event.
     * @return An EventWithTeams object containing the event and its registered teams.
     */
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventWithTeams(eventId: String): EventWithTeams?

    /**
     * Get matches for a specific event.
     * @param eventId The unique identifier of the event.
     * @return A list of match summaries for the event.
     */
    @Query("SELECT m.id, '' as homeTeamName, '' as awayTeamName, m.date, m.venue, m.homeTeamScore, m.awayTeamScore, m.status, NULL as homeTeamLogo, NULL as awayTeamLogo " +
            "FROM matches m " +
            "WHERE m.eventId = :eventId")
    suspend fun getEventMatches(eventId: String): List<MatchSummary>

    /**
     * Get events by type.
     * @param type The event type to filter by.
     * @return A list of event list items of the specified type.
     */
    @Query("SELECT id, title, description, type, startDate, endDate, location, isRegistrationOpen, 0 as teamCount, imageUrl, status, 0 as isUserRegistered FROM events WHERE type = :type")
    suspend fun getEventsByType(type: EventType): List<EventListItem>

    /**
     * Search for events by title or description.
     * @param query The search query (with % wildcards).
     * @return A list of matching event list items.
     */
    @Query("SELECT id, title, description, type, startDate, endDate, location, isRegistrationOpen, 0 as teamCount, imageUrl, status, 0 as isUserRegistered FROM events WHERE title LIKE :query OR description LIKE :query")
    suspend fun searchEvents(query: String): List<EventListItem>

    /**
     * Get events within a date range.
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A list of event list items within the date range.
     */
    @Query("SELECT id, title, description, type, startDate, endDate, location, isRegistrationOpen, 0 as teamCount, imageUrl, status, 0 as isUserRegistered FROM events WHERE (startDate BETWEEN :startDate AND :endDate) OR (endDate BETWEEN :startDate AND :endDate)")
    suspend fun getEventsByDateRange(startDate: Long, endDate: Long): List<EventListItem>

    /**
     * Insert event list items.
     * @param events The list of event list items to insert.
     */
    @Transaction
    suspend fun insertEventListItems(events: List<EventListItem>) {
        // In a real implementation, we would:
        // 1. Convert event list items to full event entities
        // 2. Insert the events
        // 3. Update team counts and other derived data
    }

    /**
     * Insert event list items with user registration info.
     * @param events The list of event list items to insert.
     * @param userId The user ID to associate with these events.
     */
    @Transaction
    suspend fun insertUserEventListItems(events: List<EventListItem>, userId: String) {
        // In a real implementation, we would:
        // 1. Convert event list items to full event entities
        // 2. Insert the events
        // 3. Insert or update user event registrations
    }

    /**
     * Insert an event registration.
     * @param registration The EventRegistration entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventRegistration(registration: EventRegistration)

    /**
     * Delete an event registration.
     * @param eventId The event ID.
     * @param teamId The team ID.
     */
    @Query("DELETE FROM event_registrations WHERE eventId = :eventId AND teamId = :teamId")
    suspend fun deleteEventRegistration(eventId: String, teamId: String)

    /**
     * Get user event registrations.
     * @param eventId The event ID.
     * @param userId The user ID.
     * @return A list of event registrations for the user.
     */
    @Query("SELECT er.* FROM event_registrations er " +
            "JOIN teams t ON er.teamId = t.id " +
            "WHERE er.eventId = :eventId AND (t.managerId = :userId OR t.coachId = :userId)")
    suspend fun getUserEventRegistrations(eventId: String, userId: String): List<EventRegistration>

    /**
     * Mark an event as registered for a user.
     * @param eventId The event ID.
     * @param userId The user ID.
     */
    @Query("UPDATE events SET isRegistered = 1 WHERE id = :eventId")
    suspend fun markEventAsRegistered(eventId: String, userId: String)

    /**
     * Mark an event as not registered for a user.
     * @param eventId The event ID.
     * @param userId The user ID.
     */
    @Query("UPDATE events SET isRegistered = 0 WHERE id = :eventId")
    suspend fun markEventAsUnregistered(eventId: String, userId: String)

    /**
     * Insert a match.
     * @param match The Match entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    /**
     * Update a match.
     * @param match The Match entity to update.
     */
    @Update
    suspend fun updateMatch(match: Match)

    /**
     * Insert match summaries for an event.
     * @param matches The list of match summaries to insert.
     * @param eventId The event ID to associate with these matches.
     */
    @Transaction
    suspend fun insertMatchSummaries(matches: List<MatchSummary>, eventId: String) {
        // In a real implementation, we would:
        // 1. Convert match summaries to full match entities
        // 2. Insert the matches
    }

    /**
     * Insert an event with its teams and matches.
     * @param eventWithTeams The EventWithTeams object to insert.
     */
    @Transaction
    suspend fun insertEventWithTeams(eventWithTeams: EventWithTeams) {
        // Insert the event
        insertEvent(eventWithTeams.event)

        // In a real implementation, we would also:
        // 1. Insert or update team summaries
        // 2. Insert or update match summaries
        // 3. Update registration status if needed
    }
}