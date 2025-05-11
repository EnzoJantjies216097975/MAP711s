package com.map711s.namibiahockey.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.map711s.namibiahockey.data.model.EventEntry
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseEventDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val eventsCollection = firestore.collection("events")

    suspend fun getEvent(eventId: String): EventEntry? {
        val documentSnapshot = eventsCollection.document(eventId).get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(EventEntry::class.java)?.copy(id = documentSnapshot.id)
        } else {
            null
        }
    }

    suspend fun saveEvent(event: EventEntry): String {
        val documentReference = eventsCollection.add(event.toHashMap()).await()
        return documentReference.id
    }

    suspend fun updateEvent(event: EventEntry) {
        eventsCollection.document(event.id).set(event.toHashMap()).await()
    }

    suspend fun deleteEvent(eventId: String) {
        eventsCollection.document(eventId).delete().await()
    }

    suspend fun getAllEvents(): List<EventEntry> {
        val querySnapshot = eventsCollection.get().await()
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(EventEntry::class.java)?.copy(id = document.id)
        }
    }
}