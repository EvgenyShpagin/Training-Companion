package com.training.companion.domain.repositories

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.BodyPart
import com.training.companion.domain.models.Exercise
import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.models.IconExercise
import com.training.companion.domain.util.getSystemLanguage

interface ExercisesRepository {

    suspend fun getExercise(
        id: Int,
        language: Language = getSystemLanguage()!!
    ): IconExercise

    suspend fun getIconList(
        topExerciseId: Int?,
        filter: ExerciseListFilter,
        language: Language = getSystemLanguage()!!
    ): List<IconExercise>

    suspend fun searchForIconExercise(
        query: String,
        filter: ExerciseListFilter,
        language: Language = getSystemLanguage()!!
    ): List<IconExercise>

    suspend fun getExtras(
        exerciseId: Int,
        language: Language = getSystemLanguage()!!
    ): ExerciseExtras

    suspend fun getTrainedBodyParts(
        exercises: List<Exercise>, language: Language = getSystemLanguage()!!
    ): List<BodyPart>
}