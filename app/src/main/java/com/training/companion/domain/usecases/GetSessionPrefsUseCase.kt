package com.training.companion.domain.usecases

import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.repositories.SessionRepository

class GetSessionPrefsUseCase(
    private val repository: SessionRepository
) {
    operator fun invoke(): SessionPrefs? {
        return repository.getSessionPrefs()
    }
}