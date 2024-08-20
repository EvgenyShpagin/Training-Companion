package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.SessionRepository

class GetCurrentSetOrdinalUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Int {
        return sessionRepository.getCurrentSetOrdinal()
    }
}
