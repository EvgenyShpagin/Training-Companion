package com.training.companion.data.models.intermediate

data class FullExerciseExtras(
    val exerciseId: Int,
    val isRepeatable: Boolean,
    val isAdjustableWeight: Boolean,
    val exerciseVideoUrl: String?,
    val equipmentId: Int,
    val equipmentTextContentId: Int,
    val workoutTypeId: Int,
)
