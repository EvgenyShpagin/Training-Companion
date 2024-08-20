package com.training.companion.domain.models

import com.training.companion.domain.enums.WorkoutType

data class PlanUpdates(
    val planId: Int?,
    val newName: String? = null,
    val newComment: String? = null,
    val newWorkoutType: WorkoutType? = null,
    val newPlanExercise: NullablePlanExercise? = null,
    val updatedPlanExercise: NullablePlanExercise? = null,
    val deletePlanExerciseById: Int? = null
) {
    val allAreNull = newName == null
            && newComment == null
            && newWorkoutType == null
            && newPlanExercise == null
            && updatedPlanExercise == null
            && deletePlanExerciseById == null
}