package com.map711s.namibiahockey.util

import coil.map.Mapper
import com.google.android.play.integrity.internal.l
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestorePaginator<T>(
    private val baseQuery: Query,
    private val pageSize: Int = 10,
    private val mapper: (DocumentSnapshot) -> T?
) {
    private var lastVisibleItem: DocumentSnapshot? = null
    private var isLastPage = false

    suspend fun loadFirstPage(): List<T> {
        if (isLastPage) return emptyList()

        val querySnapshot = baseQuery
            .limit(pageSize.toLong())
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            isLastPage = true
            return emptyList()
        }

        lastVisibleItem = querySnapshot.documents.lastOrNull()

        return querySnapshot.documents.mapNotNull { mapper(it) }
    }

    suspend fun loadNextPage(): List<T> {
        if (isLastPage || lastVisibleItem == null) return emptyList()

        val querySnapshot = baseQuery
            .startAfter(lastVisibleItem)
            .limit(pageSize.toLong())
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            isLastPage = true
            return emptyList()
        }

        lastVisibleItem = querySnapshot.documents.lastOrNull()

        return querySnapshot.documents.mapNotNull { mapper(it) }
    }

    fun reset() {
        lastVisibleItem = null
        isLastPage = false
    }
}