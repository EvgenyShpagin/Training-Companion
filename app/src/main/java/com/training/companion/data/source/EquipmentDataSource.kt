package com.training.companion.data.source

import com.training.companion.data.models.Equipment
import com.training.companion.data.room.dao.EquipmentDao

class EquipmentDataSource(private val equipmentDao: EquipmentDao) {

    suspend fun getByWorkoutType(workoutTypeId: Int): List<Equipment> {
        return equipmentDao.getByWorkoutType(workoutTypeId)
    }
}