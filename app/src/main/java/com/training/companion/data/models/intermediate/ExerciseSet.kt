package com.training.companion.data.models.intermediate

import com.training.companion.domain.models.Reps

data class ExerciseSet(
    val id: Int,
    val exerciseId: Int,
    val exerciseNameTextId: Int,
    val exerciseOrdinal: Int,
    val exerciseIconFilenameTextId: Int,
    val setsNumber: Int,
    val reps: Reps?,
    val restSeconds: Int?,
    val durationSeconds: Int?,
    val weightKg: Double?,
)