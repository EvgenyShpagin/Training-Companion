package com.training.companion.domain.usecases

import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.Flow

class GetLastCompletedSetUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Flow<CompletedSet?> {
        return sessionRepository.getLastCompletedSet()
    }
}