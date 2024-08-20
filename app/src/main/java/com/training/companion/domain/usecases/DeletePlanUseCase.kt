package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.PlanRepository

class DeletePlanUseCase(private val planRepository: PlanRepository) {

    suspend operator fun invoke(planId: Int) {
        planRepository.deletePlan(planId)
    }

}