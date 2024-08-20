package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.repositories.PlanRepository

class GetPlanWorkoutTypeUseCase(private val planRep: PlanRepository) {

    suspend operator fun invoke(planId: Int): WorkoutType {
        return planRep.getPlanWorkoutType(planId)
    }

}