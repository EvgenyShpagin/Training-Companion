package com.training.companion.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.training.companion.data.models.BodyPart
import com.training.companion.data.models.Set
import com.training.companion.data.models.Workout
import com.training.companion.data.models.intermediate.ExerciseSet
import com.training.companion.data.models.intermediate.PastWorkoutWithSets


@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPastWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSetsDataForPastWorkoutSets(setsData: List<Set>): LongArray

    @Query("SELECT id FROM workout")
    suspend fun getWorkoutIds(): LongArray

    @Transaction
    suspend fun getWorkouts(): List<PastWorkoutWithSets> {
        return getWorkoutIds().map { getWorkout(it.toInt()) }
    }

    @Query("SELECT * FROM workout WHERE id = :workoutId")
    suspend fun getPastWorkoutHeader(workoutId: Int): Workout

    @Transaction
    suspend fun getWorkout(workoutId: Int): PastWorkoutWithSets {
        val workout = getPastWorkoutHeader(workoutId)
        val workoutSets = getSets(workoutId)
        val exerciseIdList = workoutSets.map { it.exerciseId }.distinct()
        val exerciseIdArray = IntArray(exerciseIdList.count()) { exerciseIdList[it] }
        return PastWorkoutWithSets(
            workoutId,
            workout.datetime,
            workout.place,
            workoutSets,
            workout.type,
            workout.usedPlanId,
            getBodyParts(getPrimaryMusclesIds(exerciseIdArray))
        )
    }

    @Query("SELECT muscleId FROM exercise_primary_muscle WHERE exerciseId IN (:exerciseIds)")
    suspend fun getPrimaryMusclesIds(exerciseIds: IntArray): IntArray

    @Query(
        """
        SELECT
            s.id,
            e.exerciseId,
            e.nameTextContentId AS exerciseNameTextId,
            s.ordinal AS exerciseOrdinal,
            e.imageTextContentId AS exerciseIconFilenameTextId,
            s.number AS setsNumber,
            s.reps,
            s.restSeconds,
            s.durationSeconds,
            s.weightKg
        FROM
            wset s
            JOIN exercise e ON s.exerciseId = e.exerciseId
        WHERE 
            workoutId = :workoutId
    """
    )
    suspend fun getSets(workoutId: Int): List<ExerciseSet>

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
    suspend fun getBodyParts(muscleIds: IntArray): List<BodyPart>
}