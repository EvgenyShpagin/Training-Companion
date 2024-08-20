package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.TimeCountingMethod
import com.training.companion.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.collectLatest

class SetStageOnFinishUseCase(
    private val sessionRepository: SessionRepository,
    private val setStageUseCase: SetStageUseCase
) {
    suspend operator fun invoke(stage: WorkoutStage) {
        val currentStage = sessionRepository.getCurrentStage()
        val currentStagePreferences = sessionRepository.getStagePreferences(currentStage)

        if (currentStagePreferences.timeCountingMethod != TimeCountingMethod.Countdown) {
            throw IllegalStateException("ProceedToStageOnFinishUseCase : invoke : " +
                    "Can't wait for finish of not countdown timer")
        }
        sessionRepository.getStageTime().collectLatest { time ->
            if (time.totalSeconds == 0) {
                setStageUseCase.invoke(stage)
            }
        }
    }
}