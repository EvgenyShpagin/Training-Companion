package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.training.companion.data.models.Language.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Language(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val languageTag: String,
) {
    companion object {
        const val TABLE_NAME = "language"
    }
}
