package com.training.companion.domain.usecases

import com.training.companion.domain.repositories.PlanRepository

class GetPlansCountUseCase(private val planRepository: PlanRepository) {
    suspend operator fun invoke(): Int {
        return planRepository.getCount()
    }
}