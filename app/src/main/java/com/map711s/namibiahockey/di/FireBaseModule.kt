//package com.map711s.namibiahockey.di
//
//import android.content.Context
//import com.google.firebase.Firebase
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.firestore
//import com.map711s.namibiahockey.data.remote.firebase.FirebaseSetup
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//
//@Module
//@InstallIn(SingletonComponent::class)
//object FireBaseModule {
//    @Provides
//    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
//
//    @Provides
//    fun provideFirebaseFirestore(firebaseSetup: FirebaseSetup): FirebaseFirestore {
//        val firestore = Firebase.firestore
//        firebaseSetup.configureCaching(firestore)
//        return firestore
//    }
//
//    @Provides
//    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
//        return FirebaseAnalytics.getInstance(context)
//    }
//
//}