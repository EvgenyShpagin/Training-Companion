package com.training.companion.domain.models

import com.training.companion.domain.repositories.SessionRepository
import com.training.companion.domain.repositories.SessionRepository.Companion.LAST_EXERCISE_ORDINAL

class SetWorkoutSetUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(set: CompletedSet?, ordinal: Int = LAST_EXERCISE_ORDINAL) {
        sessionRepository.setWorkoutSet(set, ordinal)
    }
}
