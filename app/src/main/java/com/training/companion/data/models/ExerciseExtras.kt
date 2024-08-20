package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.ExerciseExtras.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            Exercise::class,
            ["exerciseId"],
            ["exerciseId"]
        )
    ]
)
data class ExerciseExtras(
    @PrimaryKey val id: Int,
    val exerciseId: Int,
    val isRepeatable: Boolean,
    val isAdjustableWeight: Boolean,
    val exerciseVideoUrl: String?,
) {
    companion object {
        const val TABLE_NAME = "exercise_extra"
    }
}