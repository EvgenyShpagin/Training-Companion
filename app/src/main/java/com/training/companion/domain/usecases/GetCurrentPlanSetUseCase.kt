package com.training.companion.domain.usecases

import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.models.Set
import com.training.companion.domain.repositories.SessionRepository

class GetCurrentPlanSetUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Set? {
        val prefs = sessionRepository.getSessionPrefs() as? SessionPrefs.WithPlan ?: return null
        val ordinal = sessionRepository.getCurrentSetOrdinal()
        return prefs.plan.getSetByOrdinal(ordinal)
    }
}
