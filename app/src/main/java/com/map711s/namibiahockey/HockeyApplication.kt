package com.map711s.namibiahockey

import android.app.Application
import coil.Coil
import com.google.firebase.FirebaseApp
import com.map711s.namibiahockey.util.ImageManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HockeyApplication : Application() {

    @Inject
    lateinit var imageManager: ImageManager

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize any app-wide components here.

        // Set the default Coil image loader
        Coil.setImageLoader(imageManager.imageLoader)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        // Clear memory cache when memory is low
        if (level >= TRIM_MEMORY_RUNNING_LOW) {
            imageManager.clearMemoryCache()
        }
    }


}
