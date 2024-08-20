package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.SessionRepository

class RestartStageUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke() {
        val currentStage = sessionRepository.getCurrentStage()
        sessionRepository.setStage(currentStage)
    }
}