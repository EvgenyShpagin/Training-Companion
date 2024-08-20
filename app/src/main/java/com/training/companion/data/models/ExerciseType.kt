package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.training.companion.data.models.ExerciseType.Companion.TABLE_NAME

/**
 * Cross-reference Table for Exercise and WorkoutType
 */
@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["exerciseId", "workoutTypeId"],
    foreignKeys = [
        ForeignKey(
            Exercise::class,
            ["exerciseId"],
            ["exerciseId"]
        ),
        ForeignKey(
            WorkoutType::class,
            ["id"],
            ["workoutTypeId"]
        )
    ]
)
data class ExerciseType(
    val exerciseId: Int,
    val workoutTypeId: Int,
) {
    companion object {
        const val TABLE_NAME = "exercise_type"
    }
}