package com.training.companion.data.models.intermediate

data class ExerciseExtrasBase(
    val exerciseId: Int,
    val isRepeatable: Boolean? = null,
    val isAdjustableWeight: Boolean? = null,
    val exerciseVideoUrl: String? = null
)