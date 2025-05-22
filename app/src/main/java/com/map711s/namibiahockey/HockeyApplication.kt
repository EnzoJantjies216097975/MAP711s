package com.map711s.namibiahockey

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.map711s.namibiahockey.data.migration.DatabaseMigration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HockeyApplication : Application() {

    @Inject
    lateinit var databaseMigration: DatabaseMigration

    companion object {
        private const val TAG = "HockeyApplication"
    }

    override fun onCreate() {
        super.onCreate()

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")

            // Initialize App Check with proper error handling
            initializeAppCheck()

            // Run database migrations
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    databaseMigration.runMigrations()
                    Log.d(TAG, "Database migrations completed")
                } catch (e: Exception) {
                    Log.e(TAG, "Database migration failed", e)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during application initialization", e)
        }
    }

    private fun initializeAppCheck() {
        try {
            val firebaseAppCheck = FirebaseAppCheck.getInstance()

            // For development/testing, disable App Check or use debug provider
            if (com.map711s.namibiahockey.BuildConfig.DEBUG) {
                Log.d(TAG, "Debug mode: Using Debug App Check Provider")
                firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
            } else {
                Log.d(TAG, "Release mode: Using Play Integrity App Check Provider")
                firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize App Check", e)
            // Continue without App Check in case of errors
        }
    }
}