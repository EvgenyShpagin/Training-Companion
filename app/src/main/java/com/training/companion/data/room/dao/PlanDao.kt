package com.training.companion.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.training.companion.data.models.BodyPart
import com.training.companion.data.models.Plan
import com.training.companion.data.models.Set
import com.training.companion.data.models.WorkoutType
import com.training.companion.data.models.intermediate.ExerciseSet
import com.training.companion.data.models.intermediate.PlanBeingCreatedWithExercises
import com.training.companion.data.models.intermediate.PlanUpdates
import com.training.companion.data.models.intermediate.PlanWithExercises
import com.training.companion.domain.models.Datetime

@Dao
abstract class PlanDao {

    @Transaction
    open suspend fun getPlans(): List<PlanWithExercises> {
        return getIdsOfCreatedPlans().map { getPlan(it) }
    }

    @Query(
        """
        SELECT EXISTS(
             SELECT 1 FROM wplan WHERE lower(name) = lower(:name)
        )
    """
    )
    abstract suspend fun isThereAPlan(name: String): Boolean

    @Transaction
    open suspend fun getPlan(planId: Int) = _getPlan(planId, null)

    @Query(
        """
        SELECT EXISTS(
	        SELECT 
                exercise_equipment.equipmentId 
            FROM 
                exercise_equipment 
            WHERE
                exercise_equipment.exerciseId IN (:exerciseIds)
                AND exercise_equipment.equipmentId <> 1
        )
    """
    )
    abstract suspend fun areExercisesWithEquipment(exerciseIds: IntArray): Boolean

    @Query("SELECT * FROM wset WHERE planId = :planId")
    abstract suspend fun getSets(planId: Int): List<Set>

    @Transaction
    open suspend fun updatePlan(updates: PlanUpdates) {

        var planBeingUpdatedId = updates.planId ?: getPlanBeingCreatedId()?.toInt()

        if (planBeingUpdatedId == null) {
            planBeingUpdatedId = _addPlanHeader(
                Plan(
                    name = updates.newName ?: "",
                    comment = updates.newComment,
                    workoutTypeId = updates.newWorkoutTypeId!!,
                    usedTimes = 0,
                    creationDate = null
                )
            ).toInt()
        } else {
            updates.newName?.let { _editName(planBeingUpdatedId, it) }
            updates.newComment?.let { _editComment(planBeingUpdatedId, it) }
            updates.newWorkoutTypeId?.let { _editWorkoutType(planBeingUpdatedId, it) }
        }

        updates.deletePlanExerciseById?.let {
            deleteExerciseSetById(it)
        }
        updates.newPlanExercise?.let { exercise ->
            _addSet(
                Set(
                    ordinal = exercise.exerciseOrdinal,
                    reps = exercise.reps,
                    restSeconds = exercise.restSeconds,
                    durationSeconds = exercise.durationSeconds,
                    exerciseId = exercise.exerciseId,
                    weightKg = exercise.weightKg,
                    number = exercise.setsNumber,
                    planId = planBeingUpdatedId,
                    workoutId = null,
                )
            )
        }
        updates.updatedPlanExercise?.let {
            _updateSet(
                Set(
                    id = it.id,
                    ordinal = it.exerciseOrdinal,
                    reps = it.reps,
                    restSeconds = it.restSeconds,
                    durationSeconds = it.durationSeconds,
                    exerciseId = it.exerciseId,
                    weightKg = it.weightKg,
                    planId = planBeingUpdatedId,
                    number = it.setsNumber,
                    workoutId = null
                )
            )
        }
    }

    @Transaction
    open suspend fun updatePlanDate(planId: Int, creationDate: Datetime) {
        _updateCreationDate(planId, creationDate)
    }

    @Transaction
    open suspend fun deletePlan(planId: Int) {
        _removeAllSets(planId)
        _removePlanHeader(planId)
    }

    @Transaction
    open suspend fun getPlanBeingCreated(): PlanBeingCreatedWithExercises? {
        return _getPlanBeingCreated(null)
    }

    @Query("SELECT id FROM wplan WHERE creationDate IS NULL")
    abstract suspend fun getPlanBeingCreatedId(): Long?

    @Transaction
    open suspend fun removeSets(setIDs: IntArray) {
        _removeSets(setIDs)
    }

    @Transaction
    open suspend fun getPlanWorkoutTypeId(planId: Long): Int {
        return getWorkoutTypeIdByPlanId(planId).toInt()
    }

    @Query(
        """
        SELECT 
            s.id,
            ex.exerciseId, 
            ex.nameTextContentId AS exerciseNameTextId, 
            s.ordinal AS exerciseOrdinal,
            ex.imageTextContentId AS exerciseIconFilenameTextId,
            s.number AS setsNumber, 
            s.reps, 
            s.restSeconds, 
            s.durationSeconds,
            s.weightKg
        FROM 
            wset s
            JOIN exercise ex ON s.exerciseId = ex.exerciseId
        WHERE 
            planId = :planId
            AND s.ordinal = :ordinal
    """
    )
    abstract suspend fun getExerciseSet(planId: Int, ordinal: Int): ExerciseSet?

    @Query(
        """
        SELECT 
            s.id,
            ex.exerciseId, 
            ex.nameTextContentId AS exerciseNameTextId, 
            s.ordinal AS exerciseOrdinal,
            ex.imageTextContentId AS exerciseIconFilenameTextId,
            s.number AS setsNumber, 
            s.reps,
            s.restSeconds, 
            s.durationSeconds,
            s.weightKg
        FROM 
            wset s
            JOIN exercise ex ON s.exerciseId = ex.exerciseId
        WHERE 
            planId = :planId
        ORDER BY 
            s.ordinal
    """
    )
    abstract suspend fun getExerciseSets(planId: Int): List<ExerciseSet>

    @Transaction
    open suspend fun deleteExerciseSetById(planExerciseId: Int) {
        removeSets(intArrayOf(planExerciseId))
    }

    @Transaction
    open suspend fun deleteIncompleteSets() {
        val planExerciseIDs = getIncompleteSetIDs()
        removeSets(planExerciseIDs)
    }

    @Transaction
    open suspend fun duplicate(originId: Int, nameTransform: (originName: String) -> String): Int {
        val originPlan = getPlanHeader(originId)
        var duplicatePlanName = nameTransform(originPlan.name)
        while (isThereAPlan(duplicatePlanName)) {
            duplicatePlanName = nameTransform(duplicatePlanName)
        }

        val newPlan = Plan(
            name = duplicatePlanName,
            comment = originPlan.comment,
            workoutTypeId = originPlan.workoutTypeId,
            usedTimes = 0,
            creationDate = null
        )
        val newPlanId = _addPlanHeader(newPlan).toInt()

        val sets = getSets(originId).map {
            Set(
                ordinal = it.ordinal,
                reps = it.reps,
                restSeconds = it.restSeconds,
                durationSeconds = it.durationSeconds,
                exerciseId = it.exerciseId,
                weightKg = it.weightKg,
                number = it.number,
                planId = newPlanId,
                workoutId = null
            )
        }
        _addSets(sets)
        return newPlanId
    }

    @Query("SELECT COUNT(*) FROM wplan")
    abstract suspend fun count(): Int

    @Query("SELECT s.id FROM wset s JOIN wplan p ON p.id = s.planId WHERE s.number = 0")
    protected abstract suspend fun getIncompleteSetIDs(): IntArray

    @Query("SELECT workoutTypeId FROM wplan WHERE id = :planId")
    protected abstract suspend fun getWorkoutTypeIdByPlanId(planId: Long): Long

    @Query("SELECT * FROM workout_type WHERE id = :typeId")
    protected abstract suspend fun getWorkoutType(typeId: Int): WorkoutType

    @Update
    protected abstract suspend fun _updateSet(set: Set)

    @Query("UPDATE wplan SET creationDate = :creationDate WHERE id = :planId")
    protected abstract suspend fun _updateCreationDate(planId: Int, creationDate: Datetime)

    protected open suspend fun _getPlanBeingCreated(languageId: Int? = null): PlanBeingCreatedWithExercises? {
        val planId = getPlanBeingCreatedId()?.toInt() ?: return null
        val plan = getPlanHeader(planId)
        val workoutType = getWorkoutType(plan.workoutTypeId)
        val planExercises = getExerciseSets(planId)

        return PlanBeingCreatedWithExercises(
            id = planId,
            name = plan.name,
            comment = plan.comment,
            planExercises = planExercises,
            workoutTypeId = workoutType.id
        )
    }

    @Transaction
    protected open suspend fun _getPlan(planId: Int, translationLangId: Int?): PlanWithExercises {
        val plan = getPlanHeader(planId)
        val workoutType = getWorkoutType(plan.workoutTypeId)
        val planExercises = getExerciseSets(planId)

        val exerciseIds = IntArray(planExercises.size) { i -> planExercises[i].exerciseId }
        val equipmentRequired = areExercisesWithEquipment(exerciseIds)

        val muscleIds = getPrimaryMusclesIds(exerciseIds)

        val trainedBodyParts = _getBodyPartsWithName(muscleIds)

        return PlanWithExercises(
            id = planId,
            name = plan.name,
            comment = plan.comment,
            planExercises = planExercises,
            workoutTypeId = workoutType.id,
            usedTimes = plan.usedTimes,
            creationDate = plan.creationDate,
            equipmentRequired = equipmentRequired,
            trainedBodyParts = trainedBodyParts
        )
    }

    @Query("SELECT muscleId FROM exercise_primary_muscle WHERE exerciseId IN (:exerciseIds)")
    protected abstract fun getPrimaryMusclesIds(exerciseIds: IntArray): IntArray

    @Query(
        """
        SELECT DISTINCT
            body_part.id, body_part.textContentId
        FROM
            body_part
            JOIN muscle ON muscle.bodyPartId = body_part.id
        WHERE 
            muscle.muscleId IN (:muscleIds)
    """
    )
    protected abstract fun _getBodyPartsWithName(muscleIds: IntArray): List<BodyPart>

    @Query("SELECT wplan.id FROM wplan WHERE creationDate IS NOT NULL")
    protected abstract suspend fun getIdsOfCreatedPlans(): IntArray

    @Query("SELECT * FROM wplan WHERE id = :planId")
    protected abstract suspend fun getPlanHeader(planId: Int): Plan

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun _addPlanHeader(plan: Plan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun _addSet(set: Set): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun _addSets(setsData: List<Set>): LongArray

    @Query("UPDATE wplan SET name = :newName WHERE id = :planId")
    protected abstract suspend fun _editName(planId: Int, newName: String)

    @Query("UPDATE wplan SET comment = :newComment WHERE id = :planId")
    protected abstract suspend fun _editComment(planId: Int, newComment: String)

    @Query("UPDATE wplan SET workoutTypeId = :newWorkoutTypeId WHERE id = :planId")
    protected abstract suspend fun _editWorkoutType(planId: Int, newWorkoutTypeId: Int)

    @Query("DELETE FROM wset WHERE id IN (:setsIDs)")
    protected abstract suspend fun _removeSets(setsIDs: IntArray)

    @Query("DELETE FROM wset WHERE planId = :planId")
    protected abstract suspend fun _removeAllSets(planId: Int)

    @Query("DELETE FROM wplan WHERE id = :planId")
    protected abstract suspend fun _removePlanHeader(planId: Int)
}