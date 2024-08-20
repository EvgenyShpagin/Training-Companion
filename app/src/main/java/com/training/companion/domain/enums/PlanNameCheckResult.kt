package com.training.companion.domain.enums

enum class PlanNameCheckResult {
    CORRECT,
    ALREADY_EXISTS,
    INCLUDES_PROHIBITED_CHARS,
    EMPTY
}