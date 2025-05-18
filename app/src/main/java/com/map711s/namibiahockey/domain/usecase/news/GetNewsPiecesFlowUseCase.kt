package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetNewsPiecesFlowUseCase (
    private val newsRepository: NewsRepository
) {
    operator fun invoke(): Flow<List<NewsPiece>> {
        return newsRepository.getNewsPiecesFlow()
    }
}