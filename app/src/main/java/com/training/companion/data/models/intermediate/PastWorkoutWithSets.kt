package com.training.companion.data.models.intermediate

import com.training.companion.data.models.BodyPart
import com.training.companion.domain.enums.WorkoutPlace
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Datetime

data class PastWorkoutWithSets(
    val id: Int,
    val finishDatetime: Datetime,
    val place: WorkoutPlace,
    val sets: List<ExerciseSet>,
    val type: WorkoutType,
    val usedPlanId: Int?,
    val includedBodyPart: List<BodyPart>,
)