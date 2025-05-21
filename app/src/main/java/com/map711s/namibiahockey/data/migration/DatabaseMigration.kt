package com.map711s.namibiahockey.data.migration

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseMigration @Inject constructor(
    private val firestore: FirebaseFirestore,
    context: Context
) {
    private val TAG = "DatabaseMigration"
    private val PREF_NAME = "migration_prefs"
    private val HOCKEY_TYPE_MIGRATION_KEY = "hockey_type_migration_completed"

    // Shared preferences to track migration status
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Run all necessary database migrations
     */
    suspend fun runMigrations() {
        addHockeyTypeToEvents()
        addHockeyTypeToTeams()
        // Add more migrations as needed
    }

    /**
     * Add hockey type field to all existing events
     */
    private suspend fun addHockeyTypeToEvents() {
        if (prefs.getBoolean("$HOCKEY_TYPE_MIGRATION_KEY:events", false)) {
            Log.d(TAG, "Events hockey type migration already completed")
            return
        }

        try {
            Log.d(TAG, "Starting hockey type migration for events")

            // Query for events without hockey type field
            val querySnapshot = firestore.collection("events")
                .whereEqualTo("hockeyType", null)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                Log.d(TAG, "No events found without hockey type")
                prefs.edit().putBoolean("$HOCKEY_TYPE_MIGRATION_KEY:events", true).apply()
                return
            }

            Log.d(TAG, "Found ${querySnapshot.documents.size} events to update")

            // Default all existing events to OUTDOOR
            for (document in querySnapshot.documents) {
                firestore.collection("events")
                    .document(document.id)
                    .update("hockeyType", "OUTDOOR")
                    .await()

                Log.d(TAG, "Updated event ${document.id} with default hockey type")
            }

            // Mark migration as completed
            prefs.edit().putBoolean("$HOCKEY_TYPE_MIGRATION_KEY:events", true).apply()
            Log.d(TAG, "Events hockey type migration completed")

        } catch (e: Exception) {
            Log.e(TAG, "Error during events hockey type migration", e)
            // Don't mark as completed if there was an error
        }
    }

    /**
     * Add hockey type field to all existing teams
     */
    private suspend fun addHockeyTypeToTeams() {
        if (prefs.getBoolean("$HOCKEY_TYPE_MIGRATION_KEY:teams", false)) {
            Log.d(TAG, "Teams hockey type migration already completed")
            return
        }

        try {
            Log.d(TAG, "Starting hockey type migration for teams")

            // Query for teams without hockey type field
            val querySnapshot = firestore.collection("teams")
                .whereEqualTo("hockeyType", null)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                Log.d(TAG, "No teams found without hockey type")
                prefs.edit().putBoolean("$HOCKEY_TYPE_MIGRATION_KEY:teams", true).apply()
                return
            }

            Log.d(TAG, "Found ${querySnapshot.documents.size} teams to update")

            // Default all existing teams to OUTDOOR
            for (document in querySnapshot.documents) {
                firestore.collection("teams")
                    .document(document.id)
                    .update("hockeyType", "OUTDOOR")
                    .await()

                Log.d(TAG, "Updated team ${document.id} with default hockey type")
            }

            // Mark migration as completed
            prefs.edit().putBoolean("$HOCKEY_TYPE_MIGRATION_KEY:teams", true).apply()
            Log.d(TAG, "Teams hockey type migration completed")

        } catch (e: Exception) {
            Log.e(TAG, "Error during teams hockey type migration", e)
            // Don't mark as completed if there was an error
        }
    }
}