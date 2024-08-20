package com.training.companion.domain.usecases

class FormatPlainTextUseCase {
    fun removeSpaces(text: String): String {
        return text.trim().replace(spaceRegex, " ")
    }

    companion object {
        private val spaceRegex = "\\s+".toRegex()
    }
}