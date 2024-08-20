package com.training.companion.data.repositories

import com.training.companion.data.source.EquipmentDataSource
import com.training.companion.data.source.TextContentDataSource
import com.training.companion.data.util.get
import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Equipment
import com.training.companion.domain.repositories.EquipmentRepository
import com.training.companion.toDomainEquipment

class EquipmentRepositoryImpl private constructor(
    private val dataSource: EquipmentDataSource,
    private val textDataSource: TextContentDataSource,
) : EquipmentRepository {

    override suspend fun getByWorkoutType(
        workoutType: WorkoutType,
        language: Language,
    ): List<Equipment> {
        val equipments = dataSource.getByWorkoutType(workoutType.id)
        val equipmentNameIds = equipments.map { it.textContentId }
        val equipmentNames = textDataSource.get(equipmentNameIds, language)
        return equipments.mapIndexed { index, equipment ->
            equipment.toDomainEquipment(equipmentNames[index])
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: EquipmentRepositoryImpl? = null

        fun initialize(dataSource: EquipmentDataSource, textDataSource: TextContentDataSource) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = EquipmentRepositoryImpl(dataSource, textDataSource)
                }
            }
        }

        fun get() =
            INSTANCE ?: throw IllegalStateException("EquipmentRepositoryImpl must be initialized")
    }
}