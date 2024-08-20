package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.usecases.FinishSessionUseCase
import com.training.companion.domain.usecases.GetCompletedSetsUseCase
import com.training.companion.presentation.fragments.WorkoutResultsFragmentDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutResultsViewModel(
    private val finishSession: FinishSessionUseCase,
    private val getCompletedSets: GetCompletedSetsUseCase,
) : ViewModel() {

    private val _passedSets = MutableStateFlow(emptyList<CompletedSet>())
    val passedSets = _passedSets.asStateFlow()

    fun onFinishClick(view: View) {
        viewModelScope.launch {
            _passedSets.update { getCompletedSets() }

            finishSession()
            val action = WorkoutResultsFragmentDirections.toStartWorkoutFragment()
            view.findNavController().navigate(action)
        }
    }

}