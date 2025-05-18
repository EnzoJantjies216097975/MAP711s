//package com.map711s.namibiahockey.di
//
//import android.content.Context
//import android.content.SharedPreferences
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//import com.map711s.namibiahockey.util.SecureStorageManager
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//
//@Module
//@InstallIn(SingletonComponent::class)
//object StorageModule {
//
//    @Provides
//    fun provideEncryptedSharedPreferences(
//        @ApplicationContext context: Context
//    ): SharedPreferences {
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//
//        return EncryptedSharedPreferences.create(
//            "user_prefs",
//            masterKeyAlias,
//            context,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }
//
//    @Provides
//    fun provideSecureStorageManager(@ApplicationContext context: Context): SecureStorageManager {
//        return SecureStorageManager(context)
//    }
//
//    // For non-sensitive data, use regular SharedPreferences
//    @Provides
//    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
//        return context.getSharedPreferences("hockey_prefs", Context.MODE_PRIVATE)
//    }
//
//}