package com.training.companion.domain.models

import com.training.companion.domain.enums.WorkoutType

data class PlanBeingCreated(
    val id: Int,
    val name: String,
    val planExercises: List<PlanExercise>,
    val comment: String?,
    val workoutType: WorkoutType,
)
