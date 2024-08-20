package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Set.Companion.TABLE_NAME
import com.training.companion.domain.models.Reps

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            Exercise::class,
            ["exerciseId"],
            ["exerciseId"]
        ),
        ForeignKey(
            Plan::class,
            ["id"],
            ["planId"]
        ),
        ForeignKey(
            Workout::class,
            ["id"],
            ["workoutId"]
        )
    ]
)
data class Set(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ordinal: Int,
    val reps: Reps?,
    val restSeconds: Int?,
    val durationSeconds: Int?,
    val exerciseId: Int,
    val weightKg: Double?,
    val number: Int,
    val planId: Int?,
    val workoutId: Int?
) {
    companion object {
        const val TABLE_NAME = "wset"
    }
}