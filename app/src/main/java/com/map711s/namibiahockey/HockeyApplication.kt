package com.map711s.namibiahockey

import android.app.Application
import com.google.firebase.BuildConfig
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

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        CoroutineScope(Dispatchers.IO).launch {
            databaseMigration.runMigrations()
        }

        // Initialize Firebase App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        // For development/testing, use the debug provider
        if (BuildConfig.DEBUG) {
            // Use the debug provider which accepts debug tokens
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            // For production, use the Play Integrity provider
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}