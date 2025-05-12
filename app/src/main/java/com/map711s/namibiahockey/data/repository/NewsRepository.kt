package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.model.NewsPiece
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
) {
    private val firestore = Firebase.firestore

    // Create a new news piece
    suspend fun createNewsPiece(newsPiece: NewsPiece): Result<String> {
        return try {
            val newsMap = newsPiece.toHashMap()
            val documentReference = firestore.collection("news").add(newsMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get a news piece by ID
    suspend fun getNewsPiece(newsId: String): Result<NewsPiece> {
        return try {
            val documentSnapshot = firestore.collection("news").document(newsId).get().await()
            if (documentSnapshot.exists()) {
                val newsPiece = documentSnapshot.toObject(NewsPiece::class.java)
                    ?: return Result.failure(Exception("Failed to parse news data"))
                Result.success(newsPiece.copy(id = documentSnapshot.id)) // Ensure ID is included.
            } else {
                Result.failure(Exception("News piece not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update an existing news piece
    suspend fun updateNewsPiece(newsPiece: NewsPiece): Result<Unit> {
        return try {
            val newsMap = newsPiece.toHashMap() // Use the helper function
            firestore.collection("news").document(newsPiece.id).set(newsMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete a news piece by ID
    suspend fun deleteNewsPiece(newsId: String): Result<Unit> {
        return try {
            firestore.collection("news").document(newsId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all news pieces
    suspend fun getAllNewsPieces(): Result<List<NewsPiece>> {
        Log.d("NewsRepository", "getAllNewsPieces() called")
        return try {
            Log.d("NewsRepository", "Fetching news from Firestore...")
            val querySnapshot = firestore.collection("news").get().await()
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
                        "Mapping document: ${document.id} to NewsPiece: $newsPiece, Document Data: ${document.data}"
                    )
                    newsPiece?.copy(id = document.id) // Ensure ID is set
                } catch (e: Exception) {
                    Log.e(
                        "NewsRepository",
                        "Error mapping document ${document.id}: ${e.message}, Document Data: ${document.data}",
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
}