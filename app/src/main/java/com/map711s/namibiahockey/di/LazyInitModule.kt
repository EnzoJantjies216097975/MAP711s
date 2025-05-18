//package com.map711s.namibiahockey.di
//
//import android.content.Context
//import com.google.firebase.Firebase
//import com.google.firebase.firestore.firestore
//import com.map711s.namibiahockey.data.remote.firebase.FirebaseSetup
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//
//@Module
//@InstallIn(SingletonComponent::class)
//object LazyInitModule {
//
//    @Provides
//    fun provideAppInitializer(
//        @ApplicationContext context: Context,
//        // Inject dependencies that need lazy initialization
//        firebaseSetup: FirebaseSetup
//    ): AppInitializer {
//        return AppInitializer(context, firebaseSetup)
//    }
//}
//
//class AppInitializer(
//    private val context: Context,
//    private val firebaseSetup: FirebaseSetup
//) {
//    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//
//    fun initialize() {
//        scope.launch {
//            // Initialize Firebase caching in background
//            firebaseSetup.enableOfflineForCollections(Firebase.firestore)
//        }
//    }
//}