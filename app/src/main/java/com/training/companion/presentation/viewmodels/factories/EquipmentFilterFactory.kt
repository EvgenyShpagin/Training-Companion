package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.EquipmentRepositoryImpl
import com.training.companion.domain.usecases.GetEquipmentList
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.EquipmentFilterViewModel

class EquipmentFilterFactory(
    private val setBuilder: SetDetailsBuilder
) : ViewModelProvider.Factory {

    private val equipmentRepository = EquipmentRepositoryImpl.get()
    private val getEquipmentList = GetEquipmentList(equipmentRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EquipmentFilterViewModel(
            setBuilder = setBuilder,
            getEquipmentList = getEquipmentList
        ) as T
    }
}