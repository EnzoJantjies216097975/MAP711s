package com.map711s.namibiahockey.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirebaseSetup {
    fun configureCaching(firestore: FirebaseFirestore) {
        // Configure Firestore for offline caching
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        firestore.firestoreSettings = settings
    }

    // Enable specific collections for offline caching
    fun enableOfflineForCollections(firestore: FirebaseFirestore) {
        // Cache frequently accessed collections
        val collections = listOf("users", "teams", "events", "news")

        collections.forEach { collection ->
            firestore.collection(collection)
                .limit(100) // Limit the amount of data to cache
                .get()
        }
    }
}