package com.training.companion.data.models.intermediate

import com.training.companion.data.models.BodyPart
import com.training.companion.domain.models.Datetime


data class PlanWithExercises(
    val id: Int,
    val name: String,
    val comment: String?,
    val planExercises: List<ExerciseSet>,
    val workoutTypeId: Int,
    val usedTimes: Int,
    val creationDate: Datetime?,
    val equipmentRequired: Boolean,
    val trainedBodyParts: List<BodyPart>
)