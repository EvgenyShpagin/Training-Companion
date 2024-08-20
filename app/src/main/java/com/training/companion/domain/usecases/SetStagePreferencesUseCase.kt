package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.StagePreferences
import com.training.companion.domain.repositories.SessionRepository

class SetStagePreferencesUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(stage: WorkoutStage, stagePrefs: StagePreferences) {
        sessionRepository.setStagePreferences(stage, stagePrefs)
    }
}