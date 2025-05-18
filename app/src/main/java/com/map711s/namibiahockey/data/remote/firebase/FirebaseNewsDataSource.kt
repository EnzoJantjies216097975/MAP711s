package com.map711s.namibiahockey.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.map711s.namibiahockey.data.model.NewsPiece
import kotlinx.coroutines.tasks.await

class FirebaseNewsDataSource(
    private val firestore: FirebaseFirestore
) {
    private val newsCollection = firestore.collection("news")

    suspend fun getNewsPiece(newsId: String): NewsPiece? {
        val documentSnapshot = newsCollection.document(newsId).get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(NewsPiece::class.java)?.copy(id = documentSnapshot.id)
        } else {
            null
        }
    }

    suspend fun saveNewsPiece(newsPiece: NewsPiece): String {
        val documentReference = newsCollection.add(newsPiece.toHashMap()).await()
        return documentReference.id
    }

    suspend fun updateNewsPiece(newsPiece: NewsPiece) {
        newsCollection.document(newsPiece.id).set(newsPiece.toHashMap()).await()
    }

    suspend fun deleteNewsPiece(newsId: String) {
        newsCollection.document(newsId).delete().await()
    }

    suspend fun getAllNewsPieces(): List<NewsPiece> {
        val querySnapshot = newsCollection.get().await()
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(NewsPiece::class.java)?.copy(id = document.id)
        }
    }
}