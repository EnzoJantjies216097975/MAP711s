package com.map711s.namibiahockey.presentation.news.state

import com.map711s.namibiahockey.data.model.NewsPiece

// State for a single News Piece
data class NewsState(
    val isLoading: Boolean = false,
    val newsPiece: NewsPiece? = null,
    val error: String? = null
)

