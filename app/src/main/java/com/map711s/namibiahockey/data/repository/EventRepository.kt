package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.HockeyType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val firestore = Firebase.firestore
    private val eventsCollection = firestore.collection("events")


    // Create a new event
    suspend fun createEvent(event: EventEntry): Result<String> {
        return try {
            // Get current user ID
            val userId = authRepository.getCurrentUserId() ?:
            return Result.failure(Exception("User not authenticated"))

            // Add the user ID to the event data
            val eventWithUser = event.copy(createdBy = userId)
            val eventMap = eventWithUser.toHashMap()

            val documentReference = eventsCollection.add(eventMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e("EventRepository", "Error creating event", e)
            Result.failure(e)
        }
    }

    // Get events filtered by hockey type
    suspend fun getEventsByType(hockeyType: HockeyType): Result<List<EventEntry>> {
        return try {
            Log.d("EventRepository", "Fetching ${hockeyType.name} events from Firestore")
            val querySnapshot = eventsCollection
                .whereEqualTo("hockeyType", hockeyType.name)
                .get()
                .await()

            val events = querySnapshot.documents.mapNotNull { document ->
                try {
                    val event = document.toObject(EventEntry::class.java)
                    Log.d("EventRepository", "Mapped event: $event")
                    event?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("EventRepository", "Error mapping document ${document.id}", e)
                    null
                }
            }

            Result.success(events)
        } catch (e: Exception) {
            Log.e("EventRepository", "Error fetching events by type", e)
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
                Result.success(event)
            } else {
                Result.failure(Exception("Event not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update an existing event
    suspend fun updateEvent(event: EventEntry): Result<Unit> {
        return try {
            eventsCollection.document(event.id).set(event.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete an event by ID
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection.document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all events
    suspend fun getAllEvents(): Result<List<EventEntry>> {
        Log.d("EventRepository", "getAllEvents() called") // Log entry into the function
        try {
            // 1. Log before data source interaction
            Log.d("EventRepository", "Fetching events from Firestore...")
            val querySnapshot = firestore.collection("events").get().await()
            Log.d("EventRepository", "Firestore get() successful")
            // Log success of the get()

            // 2. Log the raw data
            Log.d("EventRepository", "QuerySnapshot: $querySnapshot")
            val eventsFromSource = querySnapshot.documents
            Log.d("EventRepository", "Number of documents: ${eventsFromSource.size}")

            // 3.  Check for empty or null
            if (eventsFromSource.isEmpty()) { // Changed from == null to isEmpty()
                Log.w("EventRepository", "Firestore returned empty result") // Use warning for empty
                return Result.success(emptyList()) // Return success with empty list
            }

            // 4.  Map if necessary
            val mappedEvents: List<EventEntry> = eventsFromSource.mapNotNull { document ->
                try {
                    val event = document.toObject(EventEntry::class.java)
                    Log.d(
                        "EventRepository",
                        "Mapping document: ${document.id} to EventEntry: $event, Document Data: ${document.data}"
                    ) // Include document data
                    event?.copy(id = document.id) // Ensure the id is set.
                } catch (e: Exception) {
                    Log.e(
                        "EventRepository",
                        "Error mapping document ${document.id}: ${e.message}, Document Data: ${document.data}",
                        e
                    ) // Include document data in error log
                    null //  Important:  Return null on mapping failure, mapNotNull will remove it.
                }
            }
            Log.d("EventRepository", "Mapped events: $mappedEvents")
            return Result.success(mappedEvents)
        } catch (e: Exception) {
            // 5. Log the exception
            Log.e("EventRepository", "Error fetching events: ${e.message}", e)
            return Result.failure(e)
        } finally {
            Log.d("EventRepository", "getAllEvents() finished")
        }
    }

    // Register for an event
    suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> {
        return try {
            val eventDoc = firestore.collection("events").document(eventId).get().await()

            if (!eventDoc.exists()) {
                return Result.failure(Exception("Event not found"))
            }

            val event = eventDoc.toObject(EventEntry::class.java)
                ?: return Result.failure(Exception("Failed to parse event data"))

            // Check if user is already registered
            val isAlreadyRegistered = event.registeredUserIds.contains(userId)

            if (isAlreadyRegistered) {
                return Result.failure(Exception("Already registered for this event"))
            }

            // Add user to registeredUserIds
            val updatedUserIds = event.registeredUserIds + userId

            val updatedEvent = event.copy(
                registeredTeams = event.registeredTeams + 1,
                registeredUserIds = updatedUserIds,
                isRegistered = true // This is now user-specific in the UI
            )

            firestore.collection("events")
                .document(eventId)
                .set(updatedEvent.toHashMap())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Unregister from an event
    suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit> {
        return try {
            val eventDoc = firestore.collection("events").document(eventId).get().await()

            if (!eventDoc.exists()) {
                return Result.failure(Exception("Event not found"))
            }

            val event = eventDoc.toObject(EventEntry::class.java)
                ?: return Result.failure(Exception("Failed to parse event data"))

            // Check if user is registered
            val isRegistered = event.registeredUserIds.contains(userId)

            if (!isRegistered) {
                return Result.failure(Exception("Not registered for this event"))
            }

            // Remove user from registeredUserIds
            val updatedUserIds = event.registeredUserIds.filter { it != userId }

            val updatedEvent = event.copy(
                registeredTeams = maxOf(0, event.registeredTeams - 1),
                registeredUserIds = updatedUserIds,
                isRegistered = false // This is now user-specific in the UI
            )

            firestore.collection("events")
                .document(eventId)
                .set(updatedEvent.toHashMap())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}