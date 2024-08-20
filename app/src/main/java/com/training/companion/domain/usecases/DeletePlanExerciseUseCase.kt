package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.PlanRepository

class DeletePlanExerciseUseCase(private val planRepository: PlanRepository) {

    suspend operator fun invoke(exerciseId: Int) {
        planRepository.deletePlanExercise(exerciseId)
    }

}