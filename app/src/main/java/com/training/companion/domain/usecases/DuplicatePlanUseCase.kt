package com.training.companion.domain.usecases

import android.content.Context
import com.training.companion.R
import com.training.companion.domain.repositories.PlanRepository

class DuplicatePlanUseCase(private val planRepository: PlanRepository) {

    suspend operator fun invoke(context: Context, planId: Int): Int {
        return planRepository.duplicate(planId) { originName ->
            doDefaultNameTransform(context, originName)
        }
    }

    private fun doDefaultNameTransform(context: Context, origin: String): String {
        val copyWord = context.resources.getString(R.string.plan_name_copy_suffix_word)
        val copyMatch = matchCopyWord(origin, copyWord)
        return if (copyMatch == null) {
            "$origin $copyWord"
        } else {
            val suffixNumberAsString = copyMatch.groups["number"]?.value
            if (suffixNumberAsString == null) {
                "$origin 2"
            } else {
                val cutNumberName = origin.removeSuffix(suffixNumberAsString)
                val suffixNumber = suffixNumberAsString.toInt()
                "$cutNumberName${suffixNumber + 1}"
            }
        }
    }

    private fun matchCopyWord(originName: String, copyWord: String): MatchResult? {
        val regex = "$copyWord(\\s(?<number>\\d+))?$".toRegex()
        return regex.find(originName)
    }
}