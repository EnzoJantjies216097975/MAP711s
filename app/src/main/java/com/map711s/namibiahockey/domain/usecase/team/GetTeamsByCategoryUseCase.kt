package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.repository.TeamRepository
import javax.inject.Inject

class GetTeamsByCategoryUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(category: String): Result<List<Team>> {
        if (category.isBlank()) {
            return Result.failure(IllegalArgumentException("Category cannot be empty"))
        }
        return teamRepository.getTeamsByCategory(category)
    }
}