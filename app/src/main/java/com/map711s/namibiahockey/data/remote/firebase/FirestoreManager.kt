package com.map711s.namibiahockey.data.remote.firebase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await

class FirestoreManager(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) {
    init {
        // Enable offline persistence with unlimited cache size
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        firestore.firestoreSettings = settings
    }

    /**
     * Enable offline access for specific collections by pre-caching them
     */
    fun enableOfflineAccess() {
        // Cache frequently accessed collections with reasonable limits
        firestore.collection("events")
            .limit(50)
            .get()

        firestore.collection("news")
            .limit(20)
            .get()

        firestore.collection("teams")
            .limit(30)
            .get()
    }

    /**
     * Wait for pending writes to be sent when online
     */
    suspend fun waitForPendingWrites() {
        try {
            firestore.waitForPendingWrites().await()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }
}