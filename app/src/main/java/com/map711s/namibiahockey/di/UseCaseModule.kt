//package com.map711s.namibiahockey.di
//
//import com.map711s.namibiahockey.domain.repository.AuthRepository
//import com.map711s.namibiahockey.domain.repository.EventRepository
//import com.map711s.namibiahockey.domain.usecase.auth.GetUserProfileUseCase
//import com.map711s.namibiahockey.domain.usecase.auth.LoginUseCase
//import com.map711s.namibiahockey.domain.usecase.auth.LogoutUseCase
//import com.map711s.namibiahockey.domain.usecase.auth.RegisterUseCase
//import com.map711s.namibiahockey.domain.usecase.auth.ResetPasswordUseCase
//import com.map711s.namibiahockey.domain.usecase.event.CreateEventUseCase
//import com.map711s.namibiahockey.domain.usecase.event.GetAllEventsUseCase
//import com.map711s.namibiahockey.domain.usecase.event.GetEventUseCase
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object UseCaseModule {
//
//    // Auth use cases
//    @Provides
//    @Singleton
//    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
//        return LoginUseCase(authRepository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
//        return RegisterUseCase(authRepository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase {
//        return LogoutUseCase(authRepository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideGetUserProfileUseCase(authRepository: AuthRepository): GetUserProfileUseCase {
//        return GetUserProfileUseCase(authRepository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase {
//        return ResetPasswordUseCase(authRepository)
//    }
//
//    // Event use cases
//    @Provides
//    @Singleton
//    fun provideCreateEventUseCase(eventRepository: EventRepository): CreateEventUseCase {
//        return CreateEventUseCase(eventRepository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideGetAllEventsUseCase(eventRepository: EventRepository): GetAllEventsUseCase {
//        return GetAllEventsUseCase(eventRepository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideGetEventUseCase(eventRepository: EventRepository): GetEventUseCase {
//        return GetEventUseCase(eventRepository)
//    }
//
//    // Similarly provide other use cases
//}