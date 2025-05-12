package com.map711s.namibiahockey.util

import android.content.Context
import coil.ImageLoader
import coil.decode.Decoder
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.map711s.namibiahockey.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Create a custom image loader for the application
    val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25) // Use 25% of app memory for image cache
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.05) // Use 5% of disk space for caching
                .build()
        }
        .respectCacheHeaders(false) // Ignore cache headers for offline support
        .crossfade(true) // Enable crossfade animation
        .components {
            add(SvgDecoder.Factory() as Decoder.Factory) // Add SVG support
        }
        .apply {
            // Add debug logger in debug builds
            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            }
        }
        .build()

    // Clear image caches
    fun clearCaches() {
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }

    // Clear memory cache only
    fun clearMemoryCache() {
        imageLoader.memoryCache?.clear()
    }
}