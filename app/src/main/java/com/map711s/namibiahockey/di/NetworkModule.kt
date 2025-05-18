//package com.map711s.namibiahockey.di
//
//import com.google.firebase.BuildConfig
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//    @Provides
//    fun provideOkHttpClient(): OkHttpClient {
//        return OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = if (BuildConfig.DEBUG)
//                    HttpLoggingInterceptor.Level.BODY
//                else
//                    HttpLoggingInterceptor.Level.NONE
//            })
//            .build()
//    }
//
//    @Provides
//    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://namibiahockey.org/")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//}