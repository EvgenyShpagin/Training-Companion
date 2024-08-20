package com.training.companion.domain.usecases

import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.models.Set
import com.training.companion.domain.repositories.SessionRepository

class GetNextSetUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Set? {
        return when (val sessionPrefs = sessionRepository.getSessionPrefs()!!) {
            is SessionPrefs.WithoutPlan -> null
            is SessionPrefs.WithPlan -> {
                val nextWorkoutSetOrdinal = sessionRepository.getCurrentSetOrdinal() + 1
                sessionPrefs.plan.getSetByOrdinal(nextWorkoutSetOrdinal)
            }
        }
    }
}