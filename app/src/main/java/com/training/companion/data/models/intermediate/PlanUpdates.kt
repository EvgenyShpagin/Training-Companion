package com.training.companion.data.models.intermediate

data class PlanUpdates(
    val planId: Int?,
    val newName: String? = null,
    val newComment: String? = null,
    val newWorkoutTypeId: Int? = null,
    val newPlanExercise: ExerciseSet? = null,
    val updatedPlanExercise: ExerciseSet? = null,
    val deletePlanExerciseById: Int? = null
)
