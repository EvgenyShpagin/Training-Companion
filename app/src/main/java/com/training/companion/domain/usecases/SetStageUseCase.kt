package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.repositories.SessionRepository

class SetStageUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(stage: WorkoutStage) {
        sessionRepository.setStage(stage)
    }
}