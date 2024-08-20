package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.training.companion.domain.models.Equipment
import com.training.companion.domain.usecases.GetEquipmentList
import com.training.companion.presentation.fragments.EquipmentFilterFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EquipmentFilterViewModel(
    private val setBuilder: SetDetailsBuilder,
    private val getEquipmentList: GetEquipmentList,
) : ViewModel() {

    private var fullList: List<Equipment>? = null
    private val chosen = mutableListOf<Equipment>()

    private val areSomeItemsChangedAfterLaunch get() = chosen != setBuilder.equipment

    private val listStateIsIncorrect get() = chosen.isEmpty()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        setBuilder.equipment?.let { list ->
            chosen.addAll(list)
        }
    }


    suspend fun getEquipmentList(): List<Equipment> {
        val list = getEquipmentList(setBuilder.workoutType)
        handleEquipmentFetch(list)
        return list
    }

    fun onEquipmentClick(equipment: Equipment, isChecked: Boolean) {

        val alreadyAdded = chosen.find { it.id == equipment.id } != null

        if (isChecked && !alreadyAdded) {
            chosen.add(equipment)
        } else if (!isChecked && alreadyAdded) {
            chosen.remove(equipment)
        } else {
            return
        }

        _uiState.update { it.copy(buttonIsEnabled = chosen.isNotEmpty()) }
    }

    fun getWhichAreChosen(): List<Boolean> {
        return List(fullList!!.size) { i ->
            chosen.contains(fullList!![i])
        }
    }

    fun onBackPressed(view: View) {
        navigate(view, setBuilder.equipment)
    }

    fun onSubmitButtonClick(view: View) {
        val listToPass =
            if (areSomeItemsChangedAfterLaunch) {
                getNullIfAllChosen(chosen)
            } else {
                setBuilder.equipment?.let { getNullIfAllChosen(it) }
            }
        navigate(view, listToPass)
    }

    private fun getNullIfAllChosen(chosenEquipment: List<Equipment>?): List<Equipment>? {
        return if (chosenEquipment?.size == fullList!!.size) {
            null
        } else {
            chosenEquipment
        }
    }

    private fun handleEquipmentFetch(fullEquipmentList: List<Equipment>) {
        this.fullList = fullEquipmentList
        if (listStateIsIncorrect) {
            chosen.addAll(fullEquipmentList)
        }
    }

    private fun navigate(view: View, equipment: List<Equipment>?) {
        val navController = view.findNavController()
        val action = EquipmentFilterFragmentDirections.toExerciseChoiceFragment(
            when (setBuilder) {
                is SetDetailsBuilder.OfWorkoutSet -> {
                    setBuilder.copy(equipment = equipment)
                }

                is SetDetailsBuilder.OfPlanEdit -> {
                    setBuilder.copy(equipment = equipment)
                }
            }
        )
        navController.navigate(action)
    }

    data class UiState(val buttonIsEnabled: Boolean = true)
}