package com.training.companion.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Exercise.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            TextContent::class,
            ["id"],
            ["nameTextContentId"]
        ),
        ForeignKey(
            TextContent::class,
            ["id"],
            ["imageTextContentId"]
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exerciseId")
    val id: Int = 0,
    val nameTextContentId: Int,
    val imageTextContentId: Int,
) {
    companion object {
        const val TABLE_NAME = "exercise"
    }
}