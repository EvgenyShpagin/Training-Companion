package com.training.companion.domain.models

import com.training.companion.domain.enums.WorkoutPlace
import com.training.companion.domain.enums.WorkoutType

data class PastWorkout(
    val id: Int,
    val finishDatetime: Datetime,
    val place: WorkoutPlace,
    val sets: List<CompletedSet>,
    val type: WorkoutType,
    val usedPlanId: Int?,
    val includedBodyPart: List<BodyPart>,
) {
    val exercises = sets.distinctBy { it.exercise.id }
}