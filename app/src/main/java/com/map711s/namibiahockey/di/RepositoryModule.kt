//package com.map711s.namibiahockey.di
//
//import android.content.Context
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.map711s.namibiahockey.data.repository.AuthRepositoryImpl
//import com.map711s.namibiahockey.data.repository.EventRepositoryImpl
//import com.map711s.namibiahockey.data.repository.NewsRepositoryImpl
//import com.map711s.namibiahockey.data.repository.TeamRepositoryImpl
//import com.map711s.namibiahockey.domain.repository.AuthRepository
//import com.map711s.namibiahockey.domain.repository.EventRepository
//import com.map711s.namibiahockey.domain.repository.NewsRepository
//import com.map711s.namibiahockey.domain.repository.TeamRepository
//import com.map711s.namibiahockey.data.remote.firebase.FirebaseUserDataSource
//import com.map711s.namibiahockey.data.local.OfflineOperationQueue
//import com.map711s.namibiahockey.util.NetworkMonitor
//import com.map711s.namibiahockey.util.SecureStorageManager
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import org.checkerframework.checker.units.qual.A
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object RepositoryModule {
//
//    @Provides
//    fun provideAuthRepository(
//        firebaseAuth: FirebaseAuth,
//        firebaseFirestore: FirebaseFirestore,
//        userDataSource: FirebaseUserDataSource,
//        secureStorageManager: SecureStorageManager,
//        @ApplicationContext context: Context
//    ): AuthRepository {
//        return AuthRepositoryImpl(
//            firebaseAuth,
//            firebaseFirestore,
//            userDataSource,
//            secureStorageManager,
//            context
//        )
//    }
//
//    @Provides
//    fun provideEventRepository(
//        firestore: FirebaseFirestore,
//        networkMonitor: NetworkMonitor,
//        offlineOperationQueue: OfflineOperationQueue,
//        @ApplicationContext context: Context
//    ): EventRepository {
//        return EventRepositoryImpl(
//            firestore,
//            networkMonitor,
//            offlineOperationQueue,
//            context
//        )
//    }
//
//    @Provides
//    fun provideNewsRepository(
//        firestore: FirebaseFirestore,
//        @ApplicationContext context: Context
//    ): NewsRepository {
//        return NewsRepositoryImpl(firestore, context)
//    }
//
//    @Provides
//    fun provideTeamRepository(
//        firestore: FirebaseFirestore,
//        @ApplicationContext context: Context
//    ): TeamRepository {
//        return TeamRepositoryImpl(firestore, context)
//    }
//}