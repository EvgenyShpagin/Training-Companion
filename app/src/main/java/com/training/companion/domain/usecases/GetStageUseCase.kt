package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.Flow

class GetStageUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Flow<WorkoutStage> {
        return sessionRepository.getStage()
    }
}