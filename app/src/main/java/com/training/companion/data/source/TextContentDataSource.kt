package com.training.companion.data.source

import com.training.companion.data.room.dao.TextContentDao

class TextContentDataSource(private val textDao: TextContentDao) {

    suspend fun getOriginal(id: Int): String {
        return textDao.getOriginal(id)
    }

    suspend fun getOriginal(ids: Iterable<Int>): List<String> {
        return textDao.getOriginals(ids)
    }

    suspend fun getTranslated(id: Int, languageId: Int): String {
        return textDao.getTranslation(id, languageId)
    }

    suspend fun getTranslated(ids: Iterable<Int>, languageId: Int): List<String> {
        return textDao.getTranslations(ids, languageId)
    }
}