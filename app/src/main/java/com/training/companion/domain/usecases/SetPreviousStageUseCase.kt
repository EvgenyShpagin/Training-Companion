package com.training.companion.domain.usecases

import com.training.companion.domain.models.TimeCountingMethod
import com.training.companion.domain.repositories.SessionRepository

class SetPreviousStageUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke() {
        val prevStage = sessionRepository.getPreviousStage() ?: return
        val prevStagePreferences = sessionRepository.getStagePreferences(prevStage)
        val prevStageDuration = sessionRepository.getPreviousStageDuration()!!
        val preferencesWithPrevStageDuration = prevStagePreferences.copy(
            resumeTime = when (prevStagePreferences.timeCountingMethod) {
                TimeCountingMethod.Stopwatch -> prevStageDuration
                TimeCountingMethod.Countdown -> prevStagePreferences.initTime - prevStageDuration
            }
        )
        sessionRepository.removePassedStage(SessionRepository.LAST_PASSED_STAGE)
        sessionRepository.setStage(
            stage = prevStage,
            onetimePreferences = preferencesWithPrevStageDuration
        )
    }
}