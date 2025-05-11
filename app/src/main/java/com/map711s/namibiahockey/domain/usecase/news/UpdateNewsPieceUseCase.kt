package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.data.model.NewsPiece
import javax.inject.Inject

class UpdateNewsPieceUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(newsPiece: NewsPiece): Result<Unit> {
        // Validate news data
        if (newsPiece.id.isBlank()) {
            return Result.failure(IllegalArgumentException("News ID is required for updates"))
        }

        if (newsPiece.title.isBlank() || newsPiece.content.isBlank()) {
            return Result.failure(IllegalArgumentException("Required news fields are missing"))
        }

        return newsRepository.updateNewsPiece(newsPiece)
    }
}