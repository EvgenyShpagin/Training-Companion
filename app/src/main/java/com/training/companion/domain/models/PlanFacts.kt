package com.training.companion.domain.models


data class PlanFacts(
    val exerciseCount: Int,
    val totalSetsCount: Int,
    val equipmentIsUsed: Boolean,
    val includedBodyPartsCount: Int,
    val planUsedTimes: Int
)
