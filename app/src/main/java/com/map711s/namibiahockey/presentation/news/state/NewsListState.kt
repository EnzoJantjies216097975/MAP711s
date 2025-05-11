package com.map711s.namibiahockey.presentation.news.state

import com.map711s.namibiahockey.data.model.NewsPiece

// State for a list of News Pieces
data class NewsListState(
    val isLoading: Boolean = false,
    val newsPieces: List<NewsPiece> = emptyList(),
    val error: String? = null
)