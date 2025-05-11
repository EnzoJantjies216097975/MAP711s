package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import javax.inject.Inject

class GetAllTeamsUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(): Result<List<Team>> {
        return teamRepository.getAllTeams()
    }
}