package com.map711s.namibiahockey.di

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.remote.firebase.FirebaseEventDataSource
import com.map711s.namibiahockey.data.remote.firebase.FirebaseNewsDataSource
import com.map711s.namibiahockey.data.remote.firebase.FirebaseTeamDataSource
import com.map711s.namibiahockey.data.remote.firebase.FirebaseUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideFirebaseUserDataSource(firestore: FirebaseFirestore): FirebaseUserDataSource {
        return FirebaseUserDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseEventDataSource(firestore: FirebaseFirestore): FirebaseEventDataSource {
        return FirebaseEventDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseNewsDataSource(firestore: FirebaseFirestore): FirebaseNewsDataSource {
        return FirebaseNewsDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseTeamDataSource(firestore: FirebaseFirestore): FirebaseTeamDataSource {
        return FirebaseTeamDataSource(firestore)
    }
}