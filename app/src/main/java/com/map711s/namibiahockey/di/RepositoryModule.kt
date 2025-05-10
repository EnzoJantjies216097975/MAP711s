package com.map711s.namibiahockey.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firebaseFirestore, context)
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        firebaseFirestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): EventRepository {
        return EventRepositoryImpl(firebaseFirestore,context)
    }
}