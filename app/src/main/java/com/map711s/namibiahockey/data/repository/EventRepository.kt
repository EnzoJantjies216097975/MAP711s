package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.EventRegistration
import com.map711s.namibiahockey.data.model.GameResult
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.RegistrationStatus
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val firestore = Firebase.firestore
    private val eventsCollection = firestore.collection("events")
    private val registrationsCollection = firestore.collection("event_registrations")
    private val gameResultsCollection = firestore.collection("game_results")
    private val teamsCollection = firestore.collection("teams")

    private val TAG = "EnhancedEventRepository"

    // Create a new event
    suspend fun createEvent(event: EventEntry): Result<String> {
        return try {
            val userId = authRepository.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val eventWithUser = event.copy(createdBy = userId)
            val eventMap = eventWithUser.toHashMap()

            val documentReference = eventsCollection.add(eventMap).await()
            Log.d(TAG, "Event created: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating event", e)
            Result.failure(e)
        }
    }



    // Get events filtered by hockey type
    suspend fun getEventsByType(hockeyType: HockeyType): Result<List<EventEntry>> {
        return try {
            Log.d(TAG, "Fetching ${hockeyType.name} events from Firestore")
            val querySnapshot = eventsCollection
                .whereEqualTo("hockeyType", hockeyType.name)
                .get()
                .await()

            val userId = authRepository.getCurrentUserId()
            val events = querySnapshot.documents.mapNotNull { document ->
                try {
                    val event = document.toObject(EventEntry::class.java)
                    event?.copy(
                        id = document.id,
                        isRegistered = if (userId != null) {
                            checkUserRegistration(document.id, userId)
                        } else false,
                        registeredTeams = getEventRegistrationCount(document.id)
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping document ${document.id}", e)
                    null
                }
            }

            Result.success(events)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching events by type", e)
            Result.failure(e)
        }
    }

    // Get all events with registration status
    suspend fun getAllEvents(): Result<List<EventEntry>> {
        return try {
            Log.d(TAG, "Fetching all events from Firestore")
            val querySnapshot = eventsCollection.get().await()

            val userId = authRepository.getCurrentUserId()
            val events = querySnapshot.documents.mapNotNull { document ->
                try {
                    val event = document.toObject(EventEntry::class.java)
                    event?.copy(
                        id = document.id,
                        isRegistered = if (userId != null) {
                            checkUserRegistration(document.id, userId)
                        } else false,
                        registeredTeams = getEventRegistrationCount(document.id)
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping document ${document.id}", e)
                    null
                }
            }

            Result.success(events)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all events", e)
            Result.failure(e)
        }
    }

    // Get an event by ID
    suspend fun getEvent(eventId: String): Result<EventEntry> {
        return try {
            val documentSnapshot = eventsCollection.document(eventId).get().await()
            if (documentSnapshot.exists()) {
                val event = documentSnapshot.toObject(EventEntry::class.java)
                    ?: return Result.failure(Exception("Failed to parse event data"))

                val userId = authRepository.getCurrentUserId()
                val eventWithRegistration = event.copy(
                    id = eventId,
                    isRegistered = if (userId != null) {
                        checkUserRegistration(eventId, userId)
                    } else false,
                    registeredTeams = getEventRegistrationCount(eventId)
                )

                Result.success(eventWithRegistration)
            } else {
                Result.failure(Exception("Event not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting event: $eventId", e)
            Result.failure(e)
        }
    }

    // Update an existing event
    suspend fun updateEvent(event: EventEntry): Result<Unit> {
        return try {
            val eventMap = event.toHashMap()
            eventsCollection.document(event.id).set(eventMap).await()
            Log.d(TAG, "Event updated: ${event.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating event", e)
            Result.failure(e)
        }
    }

    // Delete an event
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            // Delete all registrations for this event first
            val registrations = registrationsCollection
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            for (registration in registrations.documents) {
                registration.reference.delete().await()
            }

            // Delete the event
            eventsCollection.document(eventId).delete().await()
            Log.d(TAG, "Event deleted: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting event", e)
            Result.failure(e)
        }
    }

    // Register for an event with proper role-based logic
    suspend fun registerForEvent(eventId: String, teamId: String): Result<Unit> {
        return try {
            val userId = authRepository.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val userProfile = authRepository.getUserProfile(userId).getOrNull()
                ?: return Result.failure(Exception("User profile not found"))

            val event = getEvent(eventId).getOrNull()
                ?: return Result.failure(Exception("Event not found"))

            val team = getTeam(teamId).getOrNull()
                ?: return Result.failure(Exception("Team not found"))

            // Check for event conflicts
            val conflictingEvents = checkEventConflicts(teamId, event.startDate)
            if (conflictingEvents.isNotEmpty()) {
                Log.w(TAG, "Event conflict detected for team $teamId on ${event.startDate}")
                // Still allow registration but log the conflict
            }

            // Verify user can register this team
            if (!canUserRegisterTeam(userProfile, team)) {
                return Result.failure(Exception("You are not authorized to register this team"))
            }

            // Check if already registered
            if (isTeamRegisteredForEvent(eventId, teamId)) {
                return Result.failure(Exception("Team is already registered for this event"))
            }

            // Create registration record
            val registration = EventRegistration(
                eventId = eventId,
                userId = userId,
                userName = userProfile.name,
                userRole = userProfile.role,
                teamId = teamId,
                teamName = team.name,
                registrationDate = Date(),
                status = RegistrationStatus.CONFIRMED
            )

            // Save registration
            registrationsCollection.add(registration.toHashMap()).await()

            Log.d(TAG, "Successfully registered team $teamId for event $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering for event", e)
            Result.failure(e)
        }
    }

    // Unregister from an event
    suspend fun unregisterFromEvent(eventId: String, teamId: String? = null): Result<Unit> {
        return try {
            val userId = authRepository.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // Find and delete the registration
            val querySnapshot = if (teamId != null) {
                registrationsCollection
                    .whereEqualTo("eventId", eventId)
                    .whereEqualTo("teamId", teamId)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
            } else {
                registrationsCollection
                    .whereEqualTo("eventId", eventId)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
            }

            if (querySnapshot.documents.isEmpty()) {
                return Result.failure(Exception("No registration found"))
            }

            // Delete all matching registrations
            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }

            Log.d(TAG, "Successfully unregistered from event $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering from event", e)
            Result.failure(e)
        }
    }

    // Get teams for a user based on their role
    suspend fun getUserTeams(userId: String): Result<List<Team>> {
        return try {
            val userProfile = authRepository.getUserProfile(userId).getOrNull()
                ?: return Result.failure(Exception("User profile not found"))

            val teams = when (userProfile.role) {
                UserRole.PLAYER -> {
                    // Get teams where user is a player
                    val querySnapshot = teamsCollection
                        .whereArrayContains("players", userId)
                        .get()
                        .await()

                    querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Team::class.java)?.copy(id = doc.id)
                    }
                }
                UserRole.COACH -> {
                    // Get teams where user is a coach
                    val querySnapshot = teamsCollection
                        .whereEqualTo("coach", userProfile.name)
                        .get()
                        .await()

                    querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Team::class.java)?.copy(id = doc.id)
                    }
                }
                UserRole.MANAGER -> {
                    // Get teams where user is a manager
                    val querySnapshot = teamsCollection
                        .whereEqualTo("manager", userProfile.name)
                        .get()
                        .await()

                    querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Team::class.java)?.copy(id = doc.id)
                    }
                }
                UserRole.ADMIN -> {
                    // Admin can register any team
                    val querySnapshot = teamsCollection.get().await()
                    querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Team::class.java)?.copy(id = doc.id)
                    }
                }
            }

            Result.success(teams)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user teams", e)
            Result.failure(e)
        }
    }

    // Check for event conflicts on the same date
    suspend fun checkEventConflicts(teamId: String, eventDate: String): List<String> {
        return try {
            // Get all registrations for this team
            val registrations = registrationsCollection
                .whereEqualTo("teamId", teamId)
                .get()
                .await()

            val conflictingEvents = mutableListOf<String>()

            for (regDoc in registrations.documents) {
                val registration = regDoc.toObject(EventRegistration::class.java)
                if (registration != null) {
                    // Get the event details
                    val eventDoc = eventsCollection.document(registration.eventId).get().await()
                    val event = eventDoc.toObject(EventEntry::class.java)

                    if (event != null && isSameDate(event.startDate, eventDate)) {
                        conflictingEvents.add(event.title)
                    }
                }
            }

            conflictingEvents
        } catch (e: Exception) {
            Log.e(TAG, "Error checking event conflicts", e)
            emptyList()
        }
    }

    // Get events registered by user/team (My Entries)
    suspend fun getUserRegisteredEvents(userId: String): Result<List<EventEntry>> {
        return try {
            val registrations = registrationsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val events = mutableListOf<EventEntry>()

            for (regDoc in registrations.documents) {
                val registration = regDoc.toObject(EventRegistration::class.java)
                if (registration != null) {
                    val eventDoc = eventsCollection.document(registration.eventId).get().await()
                    val event = eventDoc.toObject(EventEntry::class.java)

                    if (event != null) {
                        events.add(event.copy(
                            id = registration.eventId,
                            isRegistered = true
                        ))
                    }
                }
            }

            Result.success(events)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user registered events", e)
            Result.failure(e)
        }
    }

    // Get game results for past events
    suspend fun getGameResults(eventId: String): Result<List<GameResult>> {
        return try {
            val querySnapshot = gameResultsCollection
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            val gameResults = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(GameResult::class.java)?.copy(id = doc.id)
            }

            Result.success(gameResults)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting game results", e)
            Result.failure(e)
        }
    }

    // Helper functions
    private suspend fun checkUserRegistration(eventId: String, userId: String): Boolean {
        return try {
            val querySnapshot = registrationsCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            !querySnapshot.documents.isEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking user registration", e)
            false
        }
    }

    private suspend fun getEventRegistrationCount(eventId: String): Int {
        return try {
            val querySnapshot = registrationsCollection
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            // Count unique teams
            val uniqueTeams = querySnapshot.documents
                .mapNotNull { it.toObject(EventRegistration::class.java)?.teamId }
                .distinct()

            uniqueTeams.size
        } catch (e: Exception) {
            Log.e(TAG, "Error getting registration count", e)
            0
        }
    }

    private suspend fun isTeamRegisteredForEvent(eventId: String, teamId: String): Boolean {
        return try {
            val querySnapshot = registrationsCollection
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("teamId", teamId)
                .get()
                .await()

            !querySnapshot.documents.isEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking team registration", e)
            false
        }
    }

    private fun canUserRegisterTeam(user: User, team: Team): Boolean {
        return when (user.role) {
            UserRole.ADMIN -> true
            UserRole.COACH -> team.coach.contains(user.name, ignoreCase = true)
            UserRole.MANAGER -> team.manager.contains(user.name, ignoreCase = true)
            UserRole.PLAYER -> team.players.contains(user.id)
        }
    }

    private suspend fun getTeam(teamId: String): Result<Team> {
        return try {
            val doc = teamsCollection.document(teamId).get().await()
            if (doc.exists()) {
                val team = doc.toObject(Team::class.java)
                if (team != null) {
                    Result.success(team.copy(id = doc.id))
                } else {
                    Result.failure(Exception("Failed to parse team data"))
                }
            } else {
                Result.failure(Exception("Team not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isSameDate(date1: String, date2: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val d1 = formatter.parse(date1)
            val d2 = formatter.parse(date2)

            if (d1 != null && d2 != null) {
                val cal1 = java.util.Calendar.getInstance().apply { time = d1 }
                val cal2 = java.util.Calendar.getInstance().apply { time = d2 }

                cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                        cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

}