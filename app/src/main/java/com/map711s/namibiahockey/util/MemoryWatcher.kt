package com.map711s.namibiahockey.util

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import coil.Coil
import dagger.hilt.android.qualifiers.ApplicationContext

class MemoryWatcher(
    @ApplicationContext private val context: Context
) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val handler = Handler(Looper.getMainLooper())
    private val memoryCheckInterval = 30000L // 30 seconds
    private var isMonitoring = false

    fun startMonitoring() {
        if (isMonitoring) return

        isMonitoring = true
        scheduleMemoryCheck()
    }

    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun scheduleMemoryCheck() {
        if (!isMonitoring) return

        handler.postDelayed({
            checkMemoryUsage()
            scheduleMemoryCheck()
        }, memoryCheckInterval)
    }

    private fun checkMemoryUsage() {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val availableMegs = memoryInfo.availMem / 1048576L // 1024*1024
        val totalMegs = memoryInfo.totalMem / 1048576L
        val percentAvailable = 100 * availableMegs / totalMegs

        Log.d("MemoryWatcher", "Available memory: $availableMegs MB ($percentAvailable%)")

        if (percentAvailable < 15) {
            // Low memory situation
            Log.w("MemoryWatcher", "Low memory warning: $percentAvailable% available")

            // Clear caches
            clearLowPriorityCaches()
        }
    }

    private fun clearLowPriorityCaches() {
        // Clear image caches
        Coil.imageLoader(context).memoryCache?.clear()

        // Trigger garbage collection (not recommended in production, but shown for completeness)
        // System.gc()
    }
}