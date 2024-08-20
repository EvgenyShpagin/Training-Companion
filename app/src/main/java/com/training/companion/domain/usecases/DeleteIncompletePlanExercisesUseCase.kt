package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.PlanRepository

class DeleteIncompletePlanExercisesUseCase(private val planRepository: PlanRepository) {

    suspend operator fun invoke() {
        planRepository.deleteIncompletePlanExercises()
    }

}