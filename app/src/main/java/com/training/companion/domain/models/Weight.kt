package com.training.companion.domain.models

import com.training.companion.domain.enums.WeightUnit
import kotlin.math.round


data class Weight(
    val value: Double, val weightUnit: WeightUnit
) {
    init {
        assert(value >= 0.0)
    }

    val kgs = getConvertedValue(WeightUnit.Kilograms)
    val lbs = getConvertedValue(WeightUnit.Pounds)

    override fun toString(): String {
        return value.toString()
    }

    fun toStringRoundedTo100s(): String {
        val roundedValue = getRoundedTo100sValue()
        return if (roundedValue % 1.0 == 0.0) {
            roundedValue.toInt().toString()
        } else {
            roundedValue.toString()
        }
    }

    fun convert(units: WeightUnit): Weight {
        if (weightUnit == units) return this
        val convertedValue = getConvertedValue(units)
        return Weight(convertedValue, units)
    }

    fun getRoundedTo100sValue(): Double {
        return round(value * 10.0) / 10.0
    }

    private fun getConvertedValue(units: WeightUnit): Double {
        if (weightUnit == units) return value
        return when (weightUnit) {
            WeightUnit.Kilograms -> value * COEFFICIENT
            WeightUnit.Pounds -> value / COEFFICIENT
        }
    }

    companion object {
        private const val COEFFICIENT = 2.20462
    }

}