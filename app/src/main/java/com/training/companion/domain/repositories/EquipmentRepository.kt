package com.training.companion.domain.repositories

import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Equipment
import com.training.companion.domain.util.getSystemLanguage

interface EquipmentRepository {

    suspend fun getByWorkoutType(
        workoutType: WorkoutType,
        language: Language = getSystemLanguage()!!
    ): List<Equipment>

}