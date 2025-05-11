package com.map711s.namibiahockey

import android.app.Application
import coil.Coil
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.map711s.namibiahockey.di.AppInitializer
import com.map711s.namibiahockey.util.ImageManager
import com.map711s.namibiahockey.util.MemoryWatcher
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HockeyApplication : Application() {

    @Inject
    lateinit var memoryWatcher: MemoryWatcher

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var appInitializer: AppInitializer

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize any app-wide components here.

        // Start memory monitoring in debug builds
        if (BuildConfig.DEBUG) {
            memoryWatcher.startMonitoring()
        }

        // Set the default Coil image loader
        Coil.setImageLoader(imageManager.imageLoader)
        appInitializer.initialize()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        // Clear memory cache when memory is low
        when (level) {
            TRIM_MEMORY_RUNNING_MODERATE -> {
                // Moderate memory pressure
                imageManager.clearMemoryCache()
            }
            TRIM_MEMORY_RUNNING_LOW, TRIM_MEMORY_RUNNING_CRITICAL -> {
                // Critical memory pressure
                imageManager.clearCaches()
            }
        }
    }
    override fun onLowMemory() {
        super.onLowMemory()
        imageManager.clearCaches()
    }
}
