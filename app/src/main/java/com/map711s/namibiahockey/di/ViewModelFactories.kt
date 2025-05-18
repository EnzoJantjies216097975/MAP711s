package com.map711s.namibiahockey.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.map711s.namibiahockey.presentation.auth.AuthViewModel
import com.map711s.namibiahockey.presentation.events.EventViewModel
import com.map711s.namibiahockey.presentation.news.NewsViewModel
import com.map711s.namibiahockey.presentation.team.TeamViewModel

class AuthViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(ServiceLocator.authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EventViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            val dataRepository = ServiceLocator.getEventDataRepository()
            return EventViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class NewsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            val dataRepository = ServiceLocator.getNewsDataRepository()
            return NewsViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TeamViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamViewModel::class.java)) {
            val dataRepository = ServiceLocator.getTeamDataRepository()
            return TeamViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}