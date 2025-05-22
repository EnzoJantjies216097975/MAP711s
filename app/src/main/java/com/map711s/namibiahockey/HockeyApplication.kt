package com.map711s.namibiahockey

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HockeyApplication : Application() {

    companion object {
        private const val TAG = "HockeyApplication"
    }

    override fun onCreate() {
        super.onCreate()

        try {
            // Simple Firebase initialization
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed: ${e.message}")
        }
    }
}