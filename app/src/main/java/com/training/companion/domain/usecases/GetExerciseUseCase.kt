package com.training.companion.domain.usecases

import com.training.companion.domain.models.IconExercise
import com.training.companion.domain.repositories.ExercisesRepository

class GetExerciseUseCase(private val exerciseRepository: ExercisesRepository) {

    suspend operator fun invoke(exerciseId: Int): IconExercise {
        return exerciseRepository.getExercise(exerciseId)
    }

}
