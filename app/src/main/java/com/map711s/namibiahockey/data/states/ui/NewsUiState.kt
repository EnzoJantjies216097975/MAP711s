package com.map711s.namibiahockey.data.states.ui

import NewsPiece

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val newsPieces: List<NewsPiece>) : NewsUiState()
    data class Error(val message: String, val isCritical: Boolean = false) : NewsUiState()
}