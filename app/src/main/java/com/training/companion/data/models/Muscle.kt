package com.training.companion.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Muscle.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            TextContent::class,
            ["id"],
            ["textContentId"]
        ),
        ForeignKey(
            BodyPart::class,
            ["id"],
            ["bodyPartId"]
        )
    ]
)
data class Muscle(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "muscleId")
    val id: Int = 0,
    val textContentId: Int,
    val bodyPartId: Int,
) {
    companion object {
        const val TABLE_NAME = "muscle"
    }
}