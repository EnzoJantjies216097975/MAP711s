package com.map711s.namibiahockey.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.repository.AuthRepositoryImpl
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.EventRepositoryImpl
import com.map711s.namibiahockey.data.repository.NewsRepositoryImpl
import com.map711s.namibiahockey.data.repository.TeamRepositoryImpl
import com.map711s.namibiahockey.domain.repository.NewsRepository
import com.map711s.namibiahockey.domain.repository.TeamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Binds


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindTeamRepository(
        teamRepositoryImpl: TeamRepositoryImpl
    ): TeamRepository

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