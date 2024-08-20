package com.training.companion.domain.usecases

import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.repositories.ExercisesRepository

class GetExerciseExtrasUseCase(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(exerciseId: Int): ExerciseExtras {
        return exercisesRepository.getExtras(exerciseId)
    }
}