package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.data.model.NewsPiece
import javax.inject.Inject

class GetNewsPieceUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(newsId: String): Result<NewsPiece> {
        if (newsId.isBlank()) {
            return Result.failure(IllegalArgumentException("News ID cannot be empty"))
        }
        return newsRepository.getNewsPiece(newsId)
    }
}