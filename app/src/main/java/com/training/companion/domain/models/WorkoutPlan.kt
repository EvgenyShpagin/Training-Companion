package com.training.companion.domain.models

import com.training.companion.domain.enums.WorkoutType

data class WorkoutPlan(
    val id: Int,
    val name: String,
    val planExercises: List<PlanExercise>,
    val comment: String?,
    val workoutType: WorkoutType,
    val usedTimes: Int,
    val creationDate: Datetime?,
    val equipmentRequired: Boolean,
    val trainedBodyParts: List<BodyPart>
) {

    val facts get() = PlanFacts(
        exerciseCount = planExercises.count(),
        totalSetsCount = planExercises.sumOf { it.setsNumber },
        equipmentIsUsed = equipmentRequired,
        includedBodyPartsCount = trainedBodyParts.count(),
        planUsedTimes = usedTimes,
    )

    fun getSetByOrdinal(setOrdinal: Int): Set? {
        var passedSetsCount = 0
        for (i in planExercises.indices) {
            val planExercise = planExercises[i]
            passedSetsCount += planExercise.setsNumber
            if (passedSetsCount > setOrdinal) {
                return planExercise.set
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkoutPlan

        return id == other.id
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}


