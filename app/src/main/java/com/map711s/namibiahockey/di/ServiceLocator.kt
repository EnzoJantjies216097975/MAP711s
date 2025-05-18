package com.map711s.namibiahockey.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.map711s.namibiahockey.data.local.OfflineOperationQueue
import com.map711s.namibiahockey.data.remote.firebase.FirebaseEventDataSource
import com.map711s.namibiahockey.data.remote.firebase.FirebaseNewsDataSource
import com.map711s.namibiahockey.data.remote.firebase.FirebaseTeamDataSource
import com.map711s.namibiahockey.data.remote.firebase.FirebaseUserDataSource
import com.map711s.namibiahockey.data.repository.AuthRepositoryImpl
import com.map711s.namibiahockey.data.repository.EventRepositoryImpl
import com.map711s.namibiahockey.data.repository.MatchRepositoryImpl
import com.map711s.namibiahockey.data.repository.NewsRepositoryImpl
import com.map711s.namibiahockey.data.repository.PlayerRepositoryImpl
import com.map711s.namibiahockey.data.repository.TeamRepositoryImpl
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.domain.repository.EventRepository
import com.map711s.namibiahockey.domain.repository.NewsRepository
import com.map711s.namibiahockey.domain.repository.TeamRepository
import com.map711s.namibiahockey.domain.usecase.auth.LoginUseCase
import com.map711s.namibiahockey.domain.usecase.auth.RegisterUseCase
import com.map711s.namibiahockey.domain.usecase.auth.ResetPasswordUseCase
import com.map711s.namibiahockey.util.DeepLinkHandler
import com.map711s.namibiahockey.util.ImageManager
import com.map711s.namibiahockey.util.MemoryWatcher
import com.map711s.namibiahockey.util.NetworkMonitor
import com.map711s.namibiahockey.util.NotificationManager
import com.map711s.namibiahockey.util.SecureStorageManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceLocator {
    private var applicationContext: Context? = null

    // Firebase components
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Data sources
    private val firebaseUserDataSource by lazy { FirebaseUserDataSource(firebaseFirestore) }
    private val firebaseEventDataSource by lazy { FirebaseEventDataSource(firebaseFirestore) }
    private val firebaseNewsDataSource by lazy { FirebaseNewsDataSource(firebaseFirestore) }
    private val firebaseTeamDataSource by lazy { FirebaseTeamDataSource(firebaseFirestore) }

    // Utilities
    val secureStorageManager by lazy { SecureStorageManager(requireContext()) }
    val networkMonitor by lazy { NetworkMonitor(requireContext()) }
    val notificationManager by lazy { NotificationManager(requireContext()) }
    val imageManager by lazy { ImageManager(requireContext()) }
    val memoryWatcher by lazy { MemoryWatcher(requireContext()) }
    val deepLinkHandler by lazy { DeepLinkHandler() }

    // For use cases (if needed directly)
    val loginUseCase by lazy { LoginUseCase(authRepository) }
    val registerUseCase by lazy { RegisterUseCase(authRepository) }
    val resetPasswordUseCase by lazy { ResetPasswordUseCase(authRepository) }

    // For Retrofit (if used)
    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://namibiahockey.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            firebaseAuth,
            firebaseFirestore,
            firebaseUserDataSource,
            secureStorageManager,
            requireContext()
        )
    }

    val eventRepository: EventRepository by lazy {
        val offlineQueue = OfflineOperationQueue(requireContext(), networkMonitor)
        EventRepositoryImpl(
            firebaseFirestore,
            networkMonitor,
            offlineQueue,
            requireContext()
        )
    }

    val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(firebaseFirestore, requireContext())
    }

    val teamRepository: TeamRepository by lazy {
        TeamRepositoryImpl(firebaseFirestore, requireContext())
    }

    // Initialize with application context
    fun initialize(context: Context) {
        if (applicationContext == null) {
            applicationContext = context.applicationContext
        }
    }

    private fun requireContext(): Context {
        return applicationContext ?: throw IllegalStateException("ServiceLocator not initialized with context")
    }

    // Add these methods to get data repositories
    fun getEventDataRepository(): com.map711s.namibiahockey.data.repository.EventRepository {
        return com.map711s.namibiahockey.data.repository.EventRepository(firebaseFirestore)
    }

    fun getNewsDataRepository(): com.map711s.namibiahockey.data.repository.NewsRepository {
        return com.map711s.namibiahockey.data.repository.NewsRepository()
    }

    fun getTeamDataRepository(): com.map711s.namibiahockey.data.repository.TeamRepository {
        return com.map711s.namibiahockey.data.repository.TeamRepository(firebaseFirestore)
    }
}