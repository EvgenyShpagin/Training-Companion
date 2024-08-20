package com.training.companion.domain.usecases

import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.repositories.SessionRepository

class GetWorkoutTypeUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke() = when (val sessionPrefs = sessionRepository.getSessionPrefs()) {
        is SessionPrefs.WithPlan -> sessionPrefs.plan.workoutType
        is SessionPrefs.WithoutPlan -> sessionPrefs.type
        else -> throw IllegalStateException("Workout must be started before getting the workout type")
    }
}
