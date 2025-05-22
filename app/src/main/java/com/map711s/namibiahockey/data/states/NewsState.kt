package com.map711s.namibiahockey.data.states

import com.map711s.namibiahockey.data.model.NewsPiece

// State for a single News Piece
data class NewsState(
    val isLoading: Boolean = false,
    val newsPiece: NewsPiece? = null,
    val error: String? = null
)

// State for a list of News Pieces
data class NewsListState(
    val isLoading: Boolean = false,
    val newsPieces: List<NewsPiece> = emptyList(),
    val error: String? = null
)