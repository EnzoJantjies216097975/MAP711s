package com.map711s.namibiahockey.services

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.PlayerRepository
import com.map711s.namibiahockey.data.repository.TeamRepository
import com.map711s.namibiahockey.data.local.PreferencesManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    private val playerRepository: PlayerRepository,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            // Only sync if we're on Wi-Fi or mobile data is allowed
            val shouldSync = !inputData.getBoolean(KEY_REQUIRE_WIFI_ONLY, false) ||
                    preferencesManager.syncOnMobileData.value

            if (!shouldSync) {
                return@coroutineScope Result.retry()
            }

            // Sync data
            val syncJobs = listOf(
                eventRepository.syncAllEvents(),
                teamRepository.syncAllTeams(),
                playerRepository.syncAllPlayers()
            )

            // Wait for all sync jobs to complete
            syncJobs.forEach { it.join() }

            // Update last sync timestamp
            preferencesManager.updateLastSyncTimestamp()

            Result.success()
        } catch (e: Exception) {
            // If sync fails, we want to retry
            Result.retry()
        }
    }

    companion object {
        private const val KEY_REQUIRE_WIFI_ONLY = "require_wifi_only"

        // Schedule periodic sync
        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
                6, TimeUnit.HOURS, // Sync every 6 hours
                30, TimeUnit.MINUTES // Flex period
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "namibia_hockey_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
        }

        // Request immediate sync
        fun requestSync(context: Context, requireWifiOnly: Boolean = false) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(
                    if (requireWifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
                )
                .build()

            val inputData = Data.Builder()
                .putBoolean(KEY_REQUIRE_WIFI_ONLY, requireWifiOnly)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "namibia_hockey_immediate_sync",
                    ExistingWorkPolicy.REPLACE,
                    syncRequest
                )
        }
    }
}