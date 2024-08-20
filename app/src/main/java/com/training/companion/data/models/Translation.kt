package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Translation.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Language::class,
            parentColumns = ["id"],
            childColumns = ["languageId"]
        ),
        ForeignKey(
            entity = TextContent::class,
            parentColumns = ["id"],
            childColumns = ["textContentId"]
        ),
    ]
)
data class Translation(
    @PrimaryKey
    val id: Int,
    val translation: String,
    val languageId: Int,
    val textContentId: Int,
) {
    companion object {
        const val TABLE_NAME = "translation"
    }
}
