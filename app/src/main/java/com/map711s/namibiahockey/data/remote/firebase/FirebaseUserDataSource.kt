package com.map711s.namibiahockey.data.remote.firebase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    suspend fun getUser(userId: String): FirebaseUser? {
        val documentSnapshot = usersCollection.document(userId).get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(FirebaseUser::class.java)
        }else {
            null
        }
    }

    suspend fun saveUser(user: FirebaseUser) {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun updateUser(user: FirebaseUser) {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }
}