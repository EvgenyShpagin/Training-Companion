package com.training.companion.domain.repositories

import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.NullablePlanExercise
import com.training.companion.domain.models.PlanBeingCreated
import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.models.Datetime
import com.training.companion.domain.models.WorkoutPlan

interface PlanRepository {

    suspend fun getPlans(language: Language): List<WorkoutPlan>
    suspend fun getPlan(planId: Int, language: Language): WorkoutPlan
    suspend fun isThereAPlan(name: String): Boolean
    suspend fun getPlanBeingCreated(language: Language): PlanBeingCreated?
    suspend fun getPlanWorkoutType(planId: Int): WorkoutType
    suspend fun getPlanBeingCreatedId(): Int?
    suspend fun getNullablePlanExercise(planId: Int, exerciseOrdinal: Int, language: Language): NullablePlanExercise?
    suspend fun savePlanUpdates(planUpdates: PlanUpdates)
    suspend fun updatePlanDate(planId: Int, creationDate: Datetime)
    suspend fun deletePlan(planId: Int)
    suspend fun deletePlanExercise(planExerciseId: Int)
    suspend fun deleteIncompletePlanExercises()
    suspend fun duplicate(planId: Int, nameTransform: (originName: String) -> String): Int
    suspend fun getCount(): Int

}