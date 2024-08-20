package com.training.companion.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TextContentDao {

    @Query("SELECT originalText FROM text_content WHERE id =:textContentId")
    suspend fun getOriginal(textContentId: Int): String

    @Query(
        """
        SELECT 
            translation 
        FROM 
            text_content 
            JOIN translation ON translation.textContentId = text_content.id
        WHERE 
            text_content.id = :textContentId
            AND translation.languageId = :languageId
        """
    )
    suspend fun getTranslation(textContentId: Int, languageId: Int): String

    @Transaction
    suspend fun getOriginals(textContentIds: Iterable<Int>): List<String> {
        return textContentIds.map { id -> getOriginal(id) }
    }

    @Transaction
    suspend fun getTranslations(textContentIds: Iterable<Int>, languageId: Int): List<String> {
        return textContentIds.map { id -> getTranslation(id, languageId) }
    }
}