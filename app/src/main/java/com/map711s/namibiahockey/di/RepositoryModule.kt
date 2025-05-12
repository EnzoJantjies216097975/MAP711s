package com.map711s.namibiahockey.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.repository.AuthRepositoryImpl
import com.map711s.namibiahockey.data.repository.EventRepositoryImpl
import com.map711s.namibiahockey.data.repository.NewsRepositoryImpl
import com.map711s.namibiahockey.data.repository.TeamRepositoryImpl
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.domain.repository.EventRepository
import com.map711s.namibiahockey.domain.repository.NewsRepository
import com.map711s.namibiahockey.domain.repository.TeamRepository
import com.map711s.namibiahockey.data.remote.firebase.FirebaseUserDataSource
import com.map711s.namibiahockey.data.local.OfflineOperationQueue
import com.map711s.namibiahockey.util.NetworkMonitor
import com.map711s.namibiahockey.util.SecureStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        userDataSource: FirebaseUserDataSource,
        secureStorageManager: SecureStorageManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            firebaseAuth,
            firebaseFirestore,
            userDataSource,
            secureStorageManager

        )
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        firestore: FirebaseFirestore,
        networkMonitor: NetworkMonitor,
        offlineOperationQueue: OfflineOperationQueue
    ): EventRepository {
        return EventRepositoryImpl(
            firestore,
            networkMonitor,
            offlineOperationQueue

        )
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        firestore: FirebaseFirestore
    ): NewsRepository {
        return NewsRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideTeamRepository(
        firestore: FirebaseFirestore
    ): TeamRepository {
        return TeamRepositoryImpl(firestore)
    }
}