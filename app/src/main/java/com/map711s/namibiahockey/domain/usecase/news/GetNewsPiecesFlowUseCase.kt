package com.map711s.namibiahockey.domain.usecase.news

import com.map711s.namibiahockey.data.model.NewsPiece
import javax.inject.Inject

class GetNewsPiecesFlowUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(): Flow<List<NewsPiece>> {
        return newsRepository.getNewsPiecesFlow()
    }
}