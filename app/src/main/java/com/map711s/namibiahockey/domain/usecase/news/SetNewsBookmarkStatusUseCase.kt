package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.domain.repository.NewsRepository

class SetNewsBookmarkStatusUseCase (
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(newsId: String, isBookmarked: Boolean): Result<Unit> {
        if (newsId.isBlank()) {
            return Result.failure(IllegalArgumentException("News ID cannot be empty"))
        }
        return newsRepository.setBookmarkStatus(newsId, isBookmarked)
    }
}