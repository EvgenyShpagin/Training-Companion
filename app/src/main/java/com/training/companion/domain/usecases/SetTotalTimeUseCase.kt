package com.training.companion.domain.usecases

import com.training.companion.domain.models.Time
import com.training.companion.domain.repositories.SessionRepository

class SetTotalTimeUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(time: Time) {
        sessionRepository.setTotalTime(time)
    }
}