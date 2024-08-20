package com.training.companion.domain.repositories

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.Muscle

interface MusclesRepository {
    suspend fun getAll(language: Language): List<Muscle>
}