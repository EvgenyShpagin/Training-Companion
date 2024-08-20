package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.repositories.SessionRepository

class IsSetCommitNotFinished(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Boolean {
        val exerciseStageInfo =
            sessionRepository.getStageInfoOfLast(WorkoutStage.Exercise) ?: return false
        return exerciseStageInfo.workoutSet == null
    }
}
