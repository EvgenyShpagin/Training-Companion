package com.training.companion.presentation.viewmodels

import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.training.companion.R
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.GetStageTimeUseCase
import com.training.companion.domain.usecases.SetPreviousStageUseCase
import com.training.companion.presentation.dialogs.FinishConfirmDialog
import com.training.companion.presentation.fragments.SuspenseFragmentDirections
import com.training.companion.presentation.util.navigateBack
import com.training.companion.presentation.util.stopWorkoutService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SuspenseViewModel(
    private val getStageTimeUseCase: GetStageTimeUseCase,
    private val setPreviousStageUseCase: SetPreviousStageUseCase
): ViewModel() {

    private val _time = MutableStateFlow(Time.ZERO)
    val time = _time.asStateFlow()

    init {
        viewModelScope.launch {
            getStageTimeUseCase.invoke().collectLatest { time ->
                _time.update { time }
            }
        }
    }

    fun onMenuItemClick(view: View, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.finish_workout -> {
                val navController = view.findNavController()
                showWorkoutFinishConfirmDialog(navController, view)
            }
        }
        return true
    }

    fun onBackPress(view: View) {
        navigateBack(view)
    }

    private fun navigateBack(view: View) {
        viewModelScope.launch {
            setPreviousStageUseCase()
            val navController = view.findNavController()
            navController.navigateBack(R.id.to_workoutFragment)
        }
    }

    private fun showWorkoutFinishConfirmDialog(navController: NavController, view: View) {
        FinishConfirmDialog(view.context).show { _, _ ->
            stopWorkoutService(view)
            val action = SuspenseFragmentDirections.toResultsFragment()
            navController.navigate(action)
        }
    }
}