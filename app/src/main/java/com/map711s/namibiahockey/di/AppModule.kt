package com.map711s.namibiahockey.di

import coil.ImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.map711s.namibiahockey.data.repository.AuthRepositoryImpl
import com.map711s.namibiahockey.data.repository.EventRepositoryImpl
import com.map711s.namibiahockey.data.repository.NewsRepositoryImpl
import com.map711s.namibiahockey.data.repository.TeamRepositoryImpl
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.domain.repository.EventRepository
import com.map711s.namibiahockey.domain.repository.NewsRepository
import com.map711s.namibiahockey.domain.repository.TeamRepository
import com.map711s.namibiahockey.util.ImageManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideFirebaseAuth(): FirebaseAuth {
//        return Firebase.auth
//    }
//
//    @Provides
//    @Singleton
//    fun provideFirebaseFirestore(): FirebaseFirestore {
//        return Firebase.firestore
//    }
//
//    @Provides
//    @Singleton
//    fun provideFirebaseStorage(): FirebaseStorage {
//        return Firebase.storage
//    }
//
//    @Provides
//    @Singleton
//    fun provideImageLoader(imageManager: ImageManager): ImageLoader {
//        return imageManager.imageLoader
//    }
//
//}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindEventRepository(
        impl: EventRepositoryImpl
    ): EventRepository

    @Binds
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    abstract fun bindTeamRepository(
        impl: TeamRepositoryImpl
    ): TeamRepository
}