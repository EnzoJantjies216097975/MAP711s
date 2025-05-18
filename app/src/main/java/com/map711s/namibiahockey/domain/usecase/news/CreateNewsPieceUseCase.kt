package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.domain.repository.NewsRepository

class CreateNewsPieceUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(newsPiece: NewsPiece): Result<String> {
        // Validate news data
        if (newsPiece.title.isBlank() || newsPiece.content.isBlank() ||
            newsPiece.authorName.isBlank()) {
            return Result.failure(IllegalArgumentException("Required news fields are missing"))
        }

        return newsRepository.createNewsPiece(newsPiece)
    }
}