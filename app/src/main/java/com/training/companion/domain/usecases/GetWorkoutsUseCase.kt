package com.training.companion.domain.usecases

import com.training.companion.domain.models.PastWorkout
import com.training.companion.domain.repositories.WorkoutRepository

class GetWorkoutsUseCase(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(): List<PastWorkout> {
        return emptyList()
    }

}
