package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.SessionRepository


class GetCompletedSetsUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke() =
        sessionRepository.getPassedStages().filter { it.workoutSet != null }.map { it.workoutSet!! }
}