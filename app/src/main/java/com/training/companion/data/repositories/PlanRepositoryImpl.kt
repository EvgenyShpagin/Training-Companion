package com.training.companion.data.repositories

import com.training.companion.data.models.intermediate.ExerciseSet
import com.training.companion.data.models.intermediate.PlanBeingCreatedWithExercises
import com.training.companion.data.models.intermediate.PlanWithExercises
import com.training.companion.data.source.PlanDataSource
import com.training.companion.data.source.TextContentDataSource
import com.training.companion.data.util.get
import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Datetime
import com.training.companion.domain.models.NullablePlanExercise
import com.training.companion.domain.models.PlanBeingCreated
import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.repositories.PlanRepository
import com.training.companion.presentation.util.getWorkoutTypeFromId
import com.training.companion.toDataPlanUpdates
import com.training.companion.toDomainPlan
import com.training.companion.toDomainPlanBeingCreatedExercise

class PlanRepositoryImpl private constructor(
    private val dataSource: PlanDataSource,
    private val textDataSource: TextContentDataSource,
) : PlanRepository {

    override suspend fun getPlans(language: Language): List<WorkoutPlan> {
        return dataSource.getAllPlans().map { it.toDomain(language) }
    }

    private suspend fun PlanWithExercises.toDomain(language: Language): WorkoutPlan {
        val exerciseNames = getExerciseNames(planExercises, language)
        val exerciseFilenames = getExerciseFilenames(planExercises, language)
        val bodyPartNames = textDataSource.get(trainedBodyParts.map { it.textContentId }, language)
        return toDomainPlan(exerciseNames, exerciseFilenames, bodyPartNames)
    }

    private suspend fun getExerciseNames(
        planExercises: List<ExerciseSet>,
        language: Language,
    ): List<String> {
        val exerciseNameIds = planExercises.map { it.exerciseNameTextId }
        return textDataSource.get(exerciseNameIds, language)
    }

    private suspend fun getExerciseFilenames(
        planExercises: List<ExerciseSet>,
        language: Language,
    ): List<String> {
        val exerciseFilenameIds = planExercises.map { it.exerciseIconFilenameTextId }
        return textDataSource.get(exerciseFilenameIds, language)
    }

    override suspend fun getPlan(planId: Int, language: Language): WorkoutPlan {
        return dataSource.getPlan(planId).toDomain(language)
    }

    override suspend fun isThereAPlan(name: String): Boolean {
        return dataSource.isThereAPlan(name)
    }

    override suspend fun getPlanBeingCreated(language: Language): PlanBeingCreated? {
        return dataSource.getPlanBeingCreated()?.toDomain(language)
    }

    private suspend fun PlanBeingCreatedWithExercises.toDomain(language: Language): PlanBeingCreated {
        val exerciseNames = getExerciseNames(planExercises, language)
        val exerciseFilenames = getExerciseFilenames(planExercises, language)
        return toDomainPlan(exerciseNames, exerciseFilenames)
    }

    override suspend fun savePlanUpdates(planUpdates: PlanUpdates) {
        dataSource.savePlanUpdates(planUpdates.toDataPlanUpdates())
    }

    override suspend fun updatePlanDate(planId: Int, creationDate: Datetime) {
        dataSource.updatePlanDate(planId, creationDate)
    }

    override suspend fun deletePlan(planId: Int) {
        dataSource.deletePlan(planId)
    }

    override suspend fun deletePlanExercise(planExerciseId: Int) {
        dataSource.deletePlanExercise(planExerciseId)
    }

    override suspend fun deleteIncompletePlanExercises() {
        dataSource.deleteIncompletePlanExercises()
    }

    override suspend fun duplicate(
        planId: Int,
        nameTransform: (originName: String) -> String,
    ): Int {
        return dataSource.duplicate(planId, nameTransform)
    }

    override suspend fun getPlanWorkoutType(planId: Int): WorkoutType {
        return getWorkoutTypeFromId(dataSource.getPlanWorkoutType(planId.toLong()))
    }

    override suspend fun getPlanBeingCreatedId(): Int? {
        return dataSource.getPlanBeingCreatedId()
    }

    override suspend fun getNullablePlanExercise(
        planId: Int, exerciseOrdinal: Int, language: Language,
    ): NullablePlanExercise? {
        val exerciseSet = dataSource.getPlanExercise(planId, exerciseOrdinal) ?: return null
        val name = textDataSource.get(exerciseSet.exerciseNameTextId, language)
        val iconFilename = textDataSource.get(exerciseSet.exerciseIconFilenameTextId, language)
        return exerciseSet.toDomainPlanBeingCreatedExercise(name, iconFilename)
    }

    override suspend fun getCount(): Int {
        return dataSource.count()
    }

    companion object {

        @Volatile
        private var INSTANCE: PlanRepositoryImpl? = null

        fun initialize(dataSource: PlanDataSource, textDataSource: TextContentDataSource) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = PlanRepositoryImpl(dataSource, textDataSource)
                }
            }
        }

        fun get(): PlanRepositoryImpl {
            return INSTANCE ?: throw IllegalStateException("PlanRepositoryImpl must be initialized")
        }
    }
}