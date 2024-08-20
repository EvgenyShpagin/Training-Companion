package com.training.companion.domain.usecases

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.Muscle
import com.training.companion.domain.repositories.MusclesRepository

class GetMuscleList(
    private val musclesRepository: MusclesRepository,
) {
    suspend operator fun invoke(language: Language): List<Muscle> {
        return musclesRepository.getAll(language)
    }
}