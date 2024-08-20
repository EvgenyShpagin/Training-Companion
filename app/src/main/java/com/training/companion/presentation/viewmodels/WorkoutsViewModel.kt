package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.models.PastWorkout
import com.training.companion.domain.usecases.GetWorkoutsUseCase
import com.training.companion.presentation.fragments.toplevel.WorkoutsFragmentDirections
import com.training.companion.presentation.recyclerview.adapters.WorkoutsAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutsViewModel(private val getWorkouts: GetWorkoutsUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(workoutList = getWorkouts()) }
        }
    }

    val onWorkoutClickListener = WorkoutsAdapter.OnWorkoutClickListener { id ->
        //TODO: show details dialog
    }

    fun onStartClick(view: View) {
        WorkoutsFragmentDirections.toPowerWorkoutScenarioFragment().also {
            view.findNavController().navigate(it)
        }
    }

    data class UiState(val workoutList: List<PastWorkout> = emptyList())
}