package com.map711s.namibiahockey.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.map711s.namibiahockey.util.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Extension function for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "offline_operations")

class OfflineOperationQueue(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()

    // Key for storing operations in DataStore
    private val OPERATIONS_KEY = stringPreferencesKey("offline_operations")

    init {
        // Monitor network connectivity
        scope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    // Process pending operations when online
                    processQueuedOperations()
                }
            }
        }
    }

    // Add an operation to the queue
    suspend fun enqueueOperation(operation: OfflineOperation) {
        val operations = getOperations().toMutableList()
        operations.add(operation)
        saveOperations(operations)
    }

    // Get all queued operations
    suspend fun getOperations(): List<OfflineOperation> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[OPERATIONS_KEY] ?: "[]"
            val type = object : TypeToken<List<OfflineOperation>>() {}.type
            gson.fromJson(json, type) ?: emptyList<OfflineOperation>()
        }.first()
    }

    // Save operations back to storage
    private suspend fun saveOperations(operations: List<OfflineOperation>) {
        val json = gson.toJson(operations)
        context.dataStore.edit { preferences ->
            preferences[OPERATIONS_KEY] = json
        }
    }

    // Process all queued operations
    suspend fun processQueuedOperations() {
        val operations = getOperations()
        if (operations.isEmpty()) return

        val remainingOperations = mutableListOf<OfflineOperation>()

        for (operation in operations) {
            val success = processOperation(operation)
            if (!success) {
                remainingOperations.add(operation)
            }
        }

        // Save any operations that couldn't be processed
        saveOperations(remainingOperations)
    }

    // Process a single operation
    private suspend fun processOperation(operation: OfflineOperation): Boolean {
        return when (operation.type) {
            OfflineOperationType.CREATE_EVENT -> {
                // Implement the actual processing
                // This would typically involve calling your repository methods
                true // Return true if successful
            }
            OfflineOperationType.UPDATE_EVENT -> {
                // Process update
                true
            }
            OfflineOperationType.CREATE_NEWS -> {
                // Process news creation
                true
            }

            OfflineOperationType.DELETE_EVENT -> {
                true
            }

            OfflineOperationType.UPDATE_NEWS -> {
                true
            }

            OfflineOperationType.DELETE_NEWS -> {
                true
            }

            OfflineOperationType.REGISTER_FOR_EVENT -> {
                true
            }

            OfflineOperationType.UNREGISTER_FROM_EVENT -> {
                true
            }
        }
    }
}

// Define operation types
enum class OfflineOperationType {
    CREATE_EVENT,
    UPDATE_EVENT,
    DELETE_EVENT,
    CREATE_NEWS,
    UPDATE_NEWS,
    DELETE_NEWS,
    REGISTER_FOR_EVENT,
    UNREGISTER_FROM_EVENT
}

// Data class for operations
data class OfflineOperation(
    val id: String,
    val type: OfflineOperationType,
    val data: Any,
    val timestamp: Long
)