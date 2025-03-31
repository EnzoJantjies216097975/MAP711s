package com.map711s.namibiahockey.util

import kotlinx.coroutines.flow.*

/**
 * Resource class to handle different states of data loading
 */
sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String, val data: T? = null) : Resource<T>()
    class Loading<T>(val data: T? = null) : Resource<T>()

    val isSuccessful: Boolean get() = this is Success

    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message, data?.let { transform(it) })
            is Loading -> Loading(data?.let { transform(it) })
        }
    }
}

/**
 * A generic class that can provide a resource backed by both the SQLite database and the network.
 * It follows the Single Source of Truth principle.
 *
 * @param <ResultType> Type for the Resource data
 * @param <RequestType> Type for the API response
 */
class NetworkBoundResource<ResultType, RequestType>(
    private val query: () -> Flow<ResultType?>,
    private val fetch: suspend () -> RequestType,
    private val saveFetchResult: suspend (RequestType) -> Unit,
    private val shouldFetch: (ResultType?) -> Boolean = { true },
    private val onFetchError: (Throwable) -> Unit = { }
) {
    fun asFlow(): Flow<Resource<ResultType>> = flow {
        // First, emit loading with data from database
        val dbData = query().first()
        emit(Resource.Loading(dbData))

        // Decide whether to fetch from network
        val shouldFetchData = shouldFetch(dbData)

        if (shouldFetchData) {
            try {
                // Fetch from network
                val networkResult = fetch()

                // Save network result to database
                saveFetchResult(networkResult)

                // Re-emit data from database after save
                query().collect { newData ->
                    if (newData != null) {
                        emit(Resource.Success(newData))
                    }
                }
            } catch (throwable: Throwable) {
                onFetchError(throwable)

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

/**
 * Extension function to convert Flow<T> to Flow<Resource<T>>
 */
fun <T> Flow<T>.asResourceFlow(): Flow<Resource<T>> = this
    .map { Resource.Success(it) as Resource<T> }
    .onStart { emit(Resource.Loading()) }
    .catch { emit(Resource.Error(it.message ?: "Unknown error")) }
