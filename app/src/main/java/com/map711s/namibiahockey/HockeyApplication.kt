package com.map711s.namibiahockey

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HockeyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}