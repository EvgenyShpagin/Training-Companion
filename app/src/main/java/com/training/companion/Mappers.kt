package com.training.companion

import com.training.companion.data.models.Muscle
import com.training.companion.data.models.intermediate.ExerciseSet
import com.training.companion.data.models.intermediate.PastWorkoutWithSets
import com.training.companion.data.models.intermediate.PlanBeingCreatedWithExercises
import com.training.companion.data.models.intermediate.PlanWithExercises
import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.WeightUnit
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.models.NullablePlanExercise
import com.training.companion.domain.models.PlanBeingCreated
import com.training.companion.domain.models.Reps
import com.training.companion.domain.models.Set
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.Weight
import com.training.companion.presentation.util.getWorkoutTypeFromId
import com.training.companion.data.models.BodyPart as DataBodyPart
import com.training.companion.data.models.Equipment as DataEquipment
import com.training.companion.data.models.Exercise as DataExercise
import com.training.companion.data.models.intermediate.PlanUpdates as DataPlanUpdates
import com.training.companion.domain.models.BodyPart as DomainBodyPart
import com.training.companion.domain.models.Equipment as DomainEquipment
import com.training.companion.domain.models.IconExercise as DomainExercise
import com.training.companion.domain.models.IconExercise as DomainIconExercise
import com.training.companion.domain.models.Muscle as DomainMuscle
import com.training.companion.domain.models.PastWorkout as DomainPastWorkout
import com.training.companion.domain.models.PlanExercise as DomainPlanExercise
import com.training.companion.domain.models.PlanUpdates as DomainPlanUpdates
import com.training.companion.domain.models.WorkoutPlan as DomainPlan


fun PlanWithExercises.toDomainPlan(
    exerciseNames: List<String>,
    exerciseIconFilenames: List<String>,
    bodyPartNames: List<String>,
): DomainPlan {
    return DomainPlan(
        id = id,
        name = name,
        planExercises = planExercises.mapIndexed { index, set ->
            set.toDomainPlanExercise(exerciseNames[index], exerciseIconFilenames[index])
        },
        comment = comment,
        workoutType = getWorkoutTypeFromId(workoutTypeId),
        usedTimes = usedTimes,
        creationDate = creationDate,
        equipmentRequired = equipmentRequired,
        trainedBodyParts = trainedBodyParts.mapIndexed { index, bodyPart ->
            bodyPart.toDomainBodyPart(bodyPartNames[index])
        }
    )
}

fun DataBodyPart.toDomainBodyPart(name: String): DomainBodyPart {
    return DomainBodyPart(id, name)
}

fun DataExercise.toDomainExercise(name: String, iconFilename: String): DomainExercise {
    return DomainExercise(id = id, name = name, iconFilename = iconFilename)
}

fun PastWorkoutWithSets.toDomainWorkout(
    exerciseNames: List<String>,
    exerciseIconFilenames: List<String>,
    bodyPartNames: List<String>,
): DomainPastWorkout {
    return DomainPastWorkout(
        id,
        finishDatetime,
        place,
        sets.mapIndexed { index, set ->
            set.toDomainSet(
                exerciseNames[index],
                exerciseIconFilenames[index]
            )
        },
        type,
        usedPlanId,
        includedBodyPart.mapIndexed { index, bodyPart ->
            bodyPart.toDomainBodyPart(bodyPartNames[index])
        }
    )
}

fun ExerciseSet.toDomainSet(exerciseName: String, exerciseIconFilename: String): CompletedSet {
    return CompletedSet(
        ordinal = exerciseOrdinal,
        exercise = DomainExercise(id, exerciseName, exerciseIconFilename),
        restTime = Time(restSeconds!!),
        reps = reps as? Reps.Exact,
        duration = Time(durationSeconds!!),
        weight = weightKg?.let { Weight(it, WeightUnit.Kilograms) }
    )
}

fun ExerciseSet.toDomainPlanExercise(
    exerciseName: String,
    exerciseIconFilename: String,
): DomainPlanExercise {
    return DomainPlanExercise(
        id = id,
        ordinal = exerciseOrdinal,
        setsNumber = setsNumber,
        set = Set(
            exercise = DomainIconExercise(
                name = exerciseName,
                id = exerciseId,
                iconFilename = exerciseIconFilename
            ),
            reps = reps,
            restTime = restSeconds?.let { Time(it) },
            duration = durationSeconds?.let { Time(it) },
            weight = weightFromKg(weightKg)
        )
    )
}

fun Muscle.toDomainMuscle(name: String): DomainMuscle {
    return DomainMuscle(name = name)
}

val Language.id: Int
    get() = when (this) {
        Language.English -> 1
        Language.Russian -> 2
        Language.Spanish -> 3
    }


fun com.training.companion.data.models.intermediate.FullExerciseExtras.toDomainExtras(equipmentName: String): ExerciseExtras {
    val workoutType = getWorkoutTypeFromId(workoutTypeId)
    val equipment = DomainEquipment(equipmentId, equipmentName)
    return ExerciseExtras(
        exerciseId, workoutType, equipment, isRepeatable, isAdjustableWeight, exerciseVideoUrl
    )
}

fun DomainPlanUpdates.toDataPlanUpdates(): DataPlanUpdates {
    return DataPlanUpdates(
        planId,
        newName,
        newComment,
        newWorkoutType?.id,
        newPlanExercise?.toDataClearPlanExercise(),
        updatedPlanExercise?.toDataClearPlanExercise(),
        deletePlanExerciseById
    )
}

fun PlanBeingCreatedWithExercises.toDomainPlan(
    exerciseNames: List<String>,
    exerciseIconFilenames: List<String>,
): PlanBeingCreated {
    return PlanBeingCreated(
        id = id,
        name = name,
        planExercises = planExercises.mapIndexed { index, set ->
            set.toDomainPlanExercise(
                exerciseNames[index],
                exerciseIconFilenames[index]
            )
        },
        comment = comment,
        workoutType = WorkoutType.entries.find { it.id == workoutTypeId }!!
    )
}

fun ExerciseSet.toDomainPlanBeingCreatedExercise(
    exerciseName: String,
    exerciseIconFilename: String,
): NullablePlanExercise {
    return NullablePlanExercise(
        ordinal = exerciseOrdinal,
        set = Set(
            exercise = DomainIconExercise(exerciseId, exerciseName, exerciseIconFilename),
            reps = reps,
            restTime = restSeconds?.let { Time(it) },
            duration = durationSeconds?.let { Time(it) },
            weight = weightFromKg(weightKg),
        ),
        setsNumber = if (setsNumber == 0) {
            null
        } else {
            setsNumber
        },
        id = id
    )
}

fun NullablePlanExercise.toDataClearPlanExercise(): ExerciseSet {
    return ExerciseSet(
        id = id ?: -1,
        exerciseId = set.exercise!!.id,
        exerciseNameTextId = 0,
        exerciseIconFilenameTextId = 0,
        exerciseOrdinal = ordinal,
        setsNumber = setsNumber ?: 0,
        reps = set.reps,
        restSeconds = set.restTime?.totalSeconds,
        durationSeconds = set.duration?.totalSeconds,
        weightKg = set.weight?.kgs
    )
}

fun DomainPlanExercise.toNullablePlanExercise(): NullablePlanExercise {
    return NullablePlanExercise(
        id = id,
        ordinal = ordinal,
        setsNumber = setsNumber,
        set = set
    )
}

fun DataEquipment.toDomainEquipment(name: String): DomainEquipment {
    return DomainEquipment(id, name)
}

fun Collection<DomainEquipment>.idsArray(): IntArray {
    val iterator = iterator()
    return IntArray(size) { iterator.next().id }
}

private fun weightFromKg(kg: Double?): Weight? {
    return kg?.let { Weight(it, WeightUnit.Kilograms) }
}