package com.training.companion.domain.usecases

import com.training.companion.domain.models.Time

class CheckTimeIsCorrectUseCase {
    operator fun invoke(time: Time): Boolean {
        return time.totalSeconds != 0
    }
}