package com.training.companion.domain.usecases

import com.training.companion.domain.models.Time
import com.training.companion.domain.repositories.SessionRepository

class SetStageTimeUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(time: Time) {
        sessionRepository.setStageTime(time)
    }
}