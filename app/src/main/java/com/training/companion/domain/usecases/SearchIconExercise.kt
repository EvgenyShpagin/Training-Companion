package com.training.companion.domain.usecases

import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.models.IconExercise
import com.training.companion.domain.repositories.ExercisesRepository

class SearchIconExercise(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(exerciseName: String, filter: ExerciseListFilter): List<IconExercise> {
        return exercisesRepository.searchForIconExercise(exerciseName, filter)
    }
}