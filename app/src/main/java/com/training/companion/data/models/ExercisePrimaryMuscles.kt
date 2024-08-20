package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.training.companion.data.models.ExercisePrimaryMuscles.Companion.TABLE_NAME

/**
 * Cross-reference Table for Muscle and Exercise tables
 */
@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["muscleId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            Muscle::class,
            ["muscleId"],
            ["muscleId"]
        ),
        ForeignKey(
            Exercise::class,
            ["exerciseId"],
            ["exerciseId"]
        )
    ]
)
data class ExercisePrimaryMuscles(
    val muscleId: Int,
    val exerciseId: Int,
) {
    companion object {
        const val TABLE_NAME = "exercise_primary_muscle"
    }
}
