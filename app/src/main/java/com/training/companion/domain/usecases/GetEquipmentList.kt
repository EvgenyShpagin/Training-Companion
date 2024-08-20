package com.training.companion.domain.usecases

import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Equipment
import com.training.companion.domain.repositories.EquipmentRepository

class GetEquipmentList(private val repository: EquipmentRepository) {

    suspend operator fun invoke(workoutType: WorkoutType): List<Equipment> {
        return repository.getByWorkoutType(workoutType)
    }

}