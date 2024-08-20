package com.training.companion.data.util

import com.training.companion.data.source.TextContentDataSource
import com.training.companion.domain.enums.Language
import com.training.companion.domain.util.isOriginal
import com.training.companion.id

suspend fun TextContentDataSource.get(id: Int, language: Language): String {
    return if (language.isOriginal) {
        getOriginal(id)
    } else {
        getTranslated(id, language.id)
    }
}

suspend fun TextContentDataSource.get(ids: Collection<Int>, language: Language): List<String> {
    return if (language.isOriginal) {
        getOriginal(ids)
    } else {
        getTranslated(ids, language.id)
    }
}