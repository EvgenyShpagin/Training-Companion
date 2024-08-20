package com.training.companion.domain.usecases

import com.training.companion.domain.enums.PlanNameCheckResult
import com.training.companion.domain.repositories.PlanRepository

class CheckPlanNameUseCase(private val repository: PlanRepository) {
    suspend operator fun invoke(name: String?): PlanNameCheckResult {
        return if (name.isNullOrBlank()) {
            PlanNameCheckResult.EMPTY
        } else if (repository.isThereAPlan(name)) {
            PlanNameCheckResult.ALREADY_EXISTS
        } else if (!nameRegex.matches(name)) {
            PlanNameCheckResult.INCLUDES_PROHIBITED_CHARS
        } else {
            PlanNameCheckResult.CORRECT
        }
    }

    companion object {
        private val nameRegex = "^[^:$#@^*|/~\\[\\];]+$".toRegex()
    }
}