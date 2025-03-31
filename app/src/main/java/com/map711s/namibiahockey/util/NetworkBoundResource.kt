package com.map711s.namibiahockey.util

import com.map711s.namibiahockey.data.local.PreferencesManager
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

class EnhancedNetworkBoundResource<ResultType, RequestType>(
    private val query: () -> Flow<ResultType?>,
    private val fetch: suspend () -> RequestType,
    private val saveFetchResult: suspend (RequestType) -> Unit,
    private val shouldFetch: (ResultType?) -> Boolean = { true },
    private val onFetchError: (Throwable, ResultType?) -> Unit = { _, _ -> },
    private val onFetchSuccess: (RequestType) -> Unit = { },
    private val staleDuration: Long = TimeUnit.HOURS.toMillis(1),
    private val preferencesManager: PreferencesManager
) {
    fun asFlow(): Flow<Resource<ResultType>> = flow {
        // First, emit loading with data from database
        val dbData = query().first()
        emit(Resource.Loading(dbData))

        // Check if data is stale
        val isDataStale = preferencesManager.getLastSyncTimestamp() + staleDuration < System.currentTimeMillis()
        val shouldFetchData = shouldFetch(dbData) || isDataStale

        if (shouldFetchData) {
            try {
                // Fetch from network
                val networkResult = fetch()

                // Save network result to database
                saveFetchResult(networkResult)

                // Update sync timestamp
                preferencesManager.updateLastSyncTimestamp()

                // Callback for success
                onFetchSuccess(networkResult)

                // Re-emit data from database after save
                query().collect { newData ->
                    if (newData != null) {
                        emit(Resource.Success(newData))
                    }
                }
            } catch (throwable: Throwable) {
                // Handle network error but don't let it crash the flow
                onFetchError(throwable, dbData)

                // Emit error with data from database
                query().collect { newData ->
                    if (newData != null) {
                        emit(Resource.Error(throwable.message ?: "Network error", newData))
                    } else {
                        emit(Resource.Error(throwable.message ?: "Network error"))
                    }
                }
            }
        } else {
            // Not fetching from network, just emit data from database
            query().collect { newData ->
                if (newData != null) {
                    emit(Resource.Success(newData))
                } else {
                    emit(Resource.Error("No data available"))
                }
            }
        }
    }
}