package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.domain.repository.NewsRepository

class GetAllNewsPiecesUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): Result<List<NewsPiece>> {
        return newsRepository.getAllNewsPieces()
    }
}