package com.training.companion.domain.usecases

import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.repositories.PlanRepository
import com.training.companion.domain.util.getCurrentDate

class SavePlanUseCase(
    private val planRepository: PlanRepository
) {
    suspend fun saveChanges(planUpdates: PlanUpdates) {
        if (!planUpdates.allAreNull) {
            planRepository.savePlanUpdates(planUpdates)
        }
    }
    suspend fun finishChanges(planId: Int) {
        planRepository.updatePlanDate(planId, getCurrentDate())
    }
}