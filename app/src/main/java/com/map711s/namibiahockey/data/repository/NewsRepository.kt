package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.NewsPiece
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
) {
    private val firestore = Firebase.firestore
    private val newsCollection = firestore.collection("news")

    // Create a new news piece
    suspend fun createNewsPiece(newsPiece: NewsPiece): Result<String> {
        return try {
            val newsMap = newsPiece.toHashMap()
            val documentReference = newsCollection.add(newsMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error creating news piece", e)
            Result.failure(e)
        }
    }

    // Get a news piece by ID
    suspend fun getNewsPiece(newsId: String): Result<NewsPiece> {
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
            Log.e("NewsRepository", "Error getting news piece", e)
            Result.failure(e)
        }
    }

    // Update an existing news piece
    suspend fun updateNewsPiece(newsPiece: NewsPiece): Result<Unit> {
        return try {
            val newsMap = newsPiece.toHashMap()
            newsCollection.document(newsPiece.id).set(newsMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error updating news piece", e)
            Result.failure(e)
        }
    }

    // Delete a news piece by ID
    suspend fun deleteNewsPiece(newsId: String): Result<Unit> {
        return try {
            newsCollection.document(newsId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error deleting news piece", e)
            Result.failure(e)
        }
    }

    // Get all news pieces
    suspend fun getAllNewsPieces(): Result<List<NewsPiece>> {
        Log.d("NewsRepository", "getAllNewsPieces() called")
        return try {
            Log.d("NewsRepository", "Fetching news from Firestore...")
            val querySnapshot = newsCollection.get().await()
            Log.d("NewsRepository", "Firestore get() successful")

            val newsPiecesFromSource = querySnapshot.documents
            Log.d("NewsRepository", "Number of documents: ${newsPiecesFromSource.size}")

            if (newsPiecesFromSource.isEmpty()) {
                Log.w("NewsRepository", "Firestore returned empty result")
                return Result.success(emptyList())
            }

            val mappedNewsPieces: List<NewsPiece> = newsPiecesFromSource.mapNotNull { document ->
                try {
                    val newsPiece = document.toObject(NewsPiece::class.java)
                    Log.d(
                        "NewsRepository",
                        "Mapping document: ${document.id} to NewsPiece: $newsPiece"
                    )
                    newsPiece?.copy(id = document.id) // Ensure ID is set
                } catch (e: Exception) {
                    Log.e(
                        "NewsRepository",
                        "Error mapping document ${document.id}: ${e.message}",
                        e
                    )
                    null
                }
            }
            Log.d("NewsRepository", "Mapped news pieces: $mappedNewsPieces")
            return Result.success(mappedNewsPieces)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching news: ${e.message}", e)
            return Result.failure(e)
        } finally {
            Log.d("NewsRepository", "getAllNewsPieces() finished")
        }
    }

    // Get news pieces by hockey type
    suspend fun getNewsPiecesByType(hockeyType: HockeyType): Result<List<NewsPiece>> {
        return try {
            Log.d("NewsRepository", "Fetching ${hockeyType.name} news from Firestore")
            val querySnapshot = newsCollection
                .whereEqualTo("hockeyType", hockeyType.name)
                .get()
                .await()

            val newsPieces = querySnapshot.documents.mapNotNull { document ->
                try {
                    val newsPiece = document.toObject(NewsPiece::class.java)
                    Log.d("NewsRepository", "Mapped news: $newsPiece")
                    newsPiece?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("NewsRepository", "Error mapping document ${document.id}", e)
                    null
                }
            }

            Result.success(newsPieces)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching news by type", e)
            Result.failure(e)
        }
    }

    // Get bookmarked news pieces
    suspend fun getBookmarkedNewsPieces(): Result<List<NewsPiece>> {
        return try {
            Log.d("NewsRepository", "Fetching bookmarked news from Firestore")
            val querySnapshot = newsCollection
                .whereEqualTo("isBookmarked", true)
                .get()
                .await()

            val newsPieces = querySnapshot.documents.mapNotNull { document ->
                try {
                    val newsPiece = document.toObject(NewsPiece::class.java)
                    Log.d("NewsRepository", "Mapped bookmarked news: $newsPiece")
                    newsPiece?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("NewsRepository", "Error mapping document ${document.id}", e)
                    null
                }
            }

            Result.success(newsPieces)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching bookmarked news", e)
            Result.failure(e)
        }
    }
}