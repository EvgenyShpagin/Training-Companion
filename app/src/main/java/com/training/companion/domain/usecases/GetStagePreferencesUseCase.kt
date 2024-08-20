package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.StagePreferences
import com.training.companion.domain.repositories.SessionRepository

class GetStagePreferencesUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(stage: WorkoutStage): StagePreferences {
        return sessionRepository.getStagePreferences(stage)
    }
}