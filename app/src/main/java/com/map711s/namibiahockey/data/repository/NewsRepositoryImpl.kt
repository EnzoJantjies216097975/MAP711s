package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context

@Singleton
class NewsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : NewsRepository {

    private val newsCollection = firestore.collection("news")
    private val newsPiecesFlow = MutableStateFlow<List<NewsPiece>>(emptyList())

    init {
        // Initialize the flow by loading data
        refreshNews()
    }

    private fun refreshNews() {
        newsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val newsPieces = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(NewsPiece::class.java)?.copy(id = document.id)
                }
                newsPiecesFlow.value = newsPieces
            }
        }
    }

    override suspend fun createNewsPiece(newsPiece: NewsPiece): Result<String> {
        return try {
            val newsMap = newsPiece.toHashMap()
            val documentReference = newsCollection.add(newsMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNewsPiece(newsId: String): Result<NewsPiece> {
        return try {
            val documentSnapshot = newsCollection.document(newsId).get().await()
            if (documentSnapshot.exists()) {
                val newsPiece = documentSnapshot.toObject(NewsPiece::class.java)
                    ?: return Result.failure(Exception("Failed to parse news data"))
                Result.success(newsPiece.copy(id = documentSnapshot.id))
            } else {
                Result.failure(Exception("News piece not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNewsPiece(newsPiece: NewsPiece): Result<Unit> {
        return try {
            val newsMap = newsPiece.toHashMap()
            newsCollection.document(newsPiece.id).set(newsMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNewsPiece(newsId: String): Result<Unit> {
        return try {
            newsCollection.document(newsId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllNewsPieces(): Result<List<NewsPiece>> {
        return try {
            val querySnapshot = newsCollection.get().await()
            val newsPieces = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(NewsPiece::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(newsPieces)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getNewsPiecesFlow(): Flow<List<NewsPiece>> {
        return newsPiecesFlow
    }

    override suspend fun setBookmarkStatus(newsId: String, isBookmarked: Boolean): Result<Unit> {
        return try {
            newsCollection.document(newsId)
                .update("isBookmarked", isBookmarked)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}