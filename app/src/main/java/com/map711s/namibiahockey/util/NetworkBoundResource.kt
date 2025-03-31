package com.map711s.namibiahockey.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.internal.NopCollector.emit

fun <T> networkResource(
    fetch: suspend () -> T,
    onFetchError: (Throwable) -> Unit = { }
): Flow<Resource<T>> = flow {
    emit(Resource.Loading())

    try {
        val response = fetch()
        emit(Resource.Success(response))
    } catch (throwable: Throwable) {
        onFetchError(throwable)
        emit(Resource.Error(throwable.message ?: "Network error"))
    }
}