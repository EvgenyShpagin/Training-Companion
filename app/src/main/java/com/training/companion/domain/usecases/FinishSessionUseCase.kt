package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.SessionRepository

class FinishSessionUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke() {
        sessionRepository.finishSession()
    }
}
