package com.training.companion.domain.usecases

import com.training.companion.domain.models.PlanBeingCreated

class IsPlanBeingCreatedEmptyUseCase {

    operator fun invoke(plan: PlanBeingCreated): Boolean {
        return plan.planExercises.isEmpty() && plan.name.isEmpty() && plan.comment.isNullOrEmpty()
    }

}