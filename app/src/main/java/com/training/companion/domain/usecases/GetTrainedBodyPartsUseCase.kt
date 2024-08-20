package com.training.companion.domain.usecases

import com.training.companion.domain.models.BodyPart
import com.training.companion.domain.models.Exercise
import com.training.companion.domain.repositories.ExercisesRepository

class GetTrainedBodyPartsUseCase(private val repository: ExercisesRepository) {

    suspend operator fun invoke(exercises: List<Exercise>): List<BodyPart> {
        return repository.getTrainedBodyParts(exercises)
    }

}