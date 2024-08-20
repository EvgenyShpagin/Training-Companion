package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.PassedStageInfo
import com.training.companion.domain.repositories.SessionRepository

class GetLastExerciseStageInfoUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(stage: WorkoutStage): PassedStageInfo? {
        return sessionRepository.getStageInfoOfLast(stage)
    }
}
