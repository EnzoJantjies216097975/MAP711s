package com.map711s.namibiahockey

import android.app.Application
import coil.Coil
import com.map711s.namibiahockey.BuildConfig
import android.content.ComponentCallbacks2
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.map711s.namibiahockey.di.ServiceLocator
import com.map711s.namibiahockey.util.ImageManager
import com.map711s.namibiahockey.util.MemoryWatcher



class HockeyApplication : Application() {

    private lateinit var memoryWatcher: MemoryWatcher
    private lateinit var imageManager: ImageManager

    override fun onCreate() {
        try {
            super.onCreate()

            FirebaseApp.initializeApp(this)

            ServiceLocator.initialize(this)

            // Get dependencies manually
            memoryWatcher = ServiceLocator.memoryWatcher
            imageManager = ServiceLocator.imageManager



            // Start memory monitoring in debug builds
            // Use debug provider for development
            if (BuildConfig.DEBUG) {
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
            } else {
                // Use Play Integrity for production
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                )
            }

            // Set the default Coil image loader
            Coil.setImageLoader(imageManager.imageLoader)

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()


            Log.d("HockeyApp", "Application initialized successfully")
        } catch (e: Exception) {
            Log.e("HockeyApp", "Error initializing application: ${e.message}", e)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        // Clear memory cache when memory is low
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                imageManager.clearMemoryCache()
            }
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                imageManager.clearCaches()
            }
        }
    }
    override fun onLowMemory() {
        super.onLowMemory()
        imageManager.clearCaches()
    }
}
