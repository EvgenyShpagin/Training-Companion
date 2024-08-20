package com.training.companion.domain.models

import com.training.companion.domain.enums.WorkoutType

data class ExerciseExtras(
    val exerciseId: Int,
    val exerciseType: WorkoutType,
    val equipment: Equipment,
    val isRepeatable: Boolean,
    val isAdjustableWeight: Boolean,
    val exerciseVideoUrl: String?
)
