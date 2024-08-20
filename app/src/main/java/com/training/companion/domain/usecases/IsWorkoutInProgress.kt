package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.repositories.SessionRepository

class IsWorkoutInProgress(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Boolean {
        return sessionRepository.getCurrentStage() != WorkoutStage.Finished
    }
}
