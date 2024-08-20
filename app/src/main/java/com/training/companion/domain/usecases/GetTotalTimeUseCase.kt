package com.training.companion.domain.usecases

import com.training.companion.domain.models.Time
import com.training.companion.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.Flow

class GetTotalTimeUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Flow<Time> {
        return sessionRepository.getTotalTime()
    }
}