package com.training.companion.domain.usecases

import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.models.IconExercise
import com.training.companion.domain.repositories.ExercisesRepository

class GetIconExerciseList(
    private val exerciseRepository: ExercisesRepository
) {
    suspend operator fun invoke(
        filter: ExerciseListFilter,
        topExerciseId: Int? = null
    ): List<IconExercise> {
        return exerciseRepository.getIconList(topExerciseId, filter)
    }
}
