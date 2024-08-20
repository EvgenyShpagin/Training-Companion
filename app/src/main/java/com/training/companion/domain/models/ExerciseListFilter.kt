package com.training.companion.domain.models

import androidx.annotation.Size
import com.training.companion.domain.enums.WorkoutType

data class ExerciseListFilter(
    val byWorkoutType: WorkoutType,
    @Size(min = 1) val byEquipmentCode: List<Equipment>? = allTypeEquipmentCode
) {
    companion object {
        val allTypeEquipmentCode: List<Equipment>? = null
    }
}
