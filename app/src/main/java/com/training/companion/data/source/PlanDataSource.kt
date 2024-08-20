package com.training.companion.data.source

import com.training.companion.data.models.intermediate.ExerciseSet
import com.training.companion.data.models.intermediate.PlanBeingCreatedWithExercises
import com.training.companion.data.models.intermediate.PlanUpdates
import com.training.companion.data.models.intermediate.PlanWithExercises
import com.training.companion.data.room.dao.PlanDao
import com.training.companion.domain.models.Datetime

class PlanDataSource(private val dao: PlanDao) {

    suspend fun getAllPlans(): List<PlanWithExercises> {
        return dao.getPlans()
    }

    suspend fun getPlan(planId: Int): PlanWithExercises {
        return dao.getPlan(planId)
    }

    suspend fun isThereAPlan(name: String): Boolean {
        return dao.isThereAPlan(name)
    }

    suspend fun savePlanUpdates(planUpdates: PlanUpdates) {
        dao.updatePlan(planUpdates)
    }

    suspend fun updatePlanDate(planId: Int, creationDate: Datetime) {
        dao.updatePlanDate(planId, creationDate)
    }

    suspend fun deletePlan(planId: Int) {
        dao.deletePlan(planId)
    }

    suspend fun getPlanBeingCreated(): PlanBeingCreatedWithExercises? {
        return dao.getPlanBeingCreated()
    }


    suspend fun getPlanWorkoutType(planId: Long): Int {
        return dao.getPlanWorkoutTypeId(planId)
    }

    suspend fun getPlanExercise(planId: Int, exerciseOrdinal: Int): ExerciseSet? {
        return dao.getExerciseSet(planId, exerciseOrdinal)
    }

    suspend fun getPlanBeingCreatedId(): Int? {
        return dao.getPlanBeingCreatedId()?.toInt()
    }

    suspend fun deletePlanExercise(planExerciseId: Int) {
        return dao.removeSets(intArrayOf(planExerciseId))
    }

    suspend fun deleteIncompletePlanExercises() {
        return dao.deleteIncompleteSets()
    }

    suspend fun duplicate(planId: Int, nameTransform: (originName: String) -> String): Int {
        return dao.duplicate(planId, nameTransform)
    }

    suspend fun count(): Int {
        return dao.count()
    }
}