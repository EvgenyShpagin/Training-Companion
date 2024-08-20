package com.training.companion.domain.usecases

import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.repositories.SessionRepository

class SetSessionPrefsUseCase(private val repository: SessionRepository) {
    operator fun invoke(prefs: SessionPrefs) {
        repository.setSessionPrefs(prefs)
    }
}