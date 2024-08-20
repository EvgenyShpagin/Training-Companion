package com.training.companion.domain.models

import com.training.companion.domain.enums.LengthUnit
import com.training.companion.domain.enums.WeightUnit

data class Units(
    val lengthUnit: LengthUnit,
    val weightUnit: WeightUnit
)
