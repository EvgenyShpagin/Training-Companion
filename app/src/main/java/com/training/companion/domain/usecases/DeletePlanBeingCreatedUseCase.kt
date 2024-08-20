package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.PlanRepository

class DeletePlanBeingCreatedUseCase(private val planRepository: PlanRepository) {
    suspend operator fun invoke(): Boolean {
        val idOfPlanBeingCreated = planRepository.getPlanBeingCreatedId()
        idOfPlanBeingCreated?.let {
            planRepository.deletePlan(idOfPlanBeingCreated)
            return true
        }
        return false
    }
}