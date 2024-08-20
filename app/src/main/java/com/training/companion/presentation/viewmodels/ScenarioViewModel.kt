package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.training.companion.BR
import com.training.companion.R
import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.usecases.GetPlanUseCase
import com.training.companion.domain.usecases.GetPlansCountUseCase
import com.training.companion.domain.usecases.GetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.fragments.PowerWorkoutScenarioFragmentDirections
import com.training.companion.presentation.viewmodels.base.StartingWorkoutBaseViewModel
import com.training.companion.presentation.util.startWorkoutService
import kotlinx.coroutines.launch

class ScenarioViewModel(
    getSessionPrefsUseCase: GetSessionPrefsUseCase,
    setSessionPrefsUseCase: SetSessionPrefsUseCase,
    private val getPlansCountUseCase: GetPlansCountUseCase,
    private val chosenPlanId: Int,
    private val getPlan: GetPlanUseCase,
    private val setStage: SetStageUseCase,
) : StartingWorkoutBaseViewModel(getSessionPrefsUseCase, setSessionPrefsUseCase) {

    @Bindable
    var uiState: UiState = if (chosenPlanId != -1) UiState.SelectedPlanUse else UiState.NotSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.uiState)
        }

    fun returnToPreviousFragment(view: View) {
        view.findNavController().popBackStack()
        clear()
    }

    fun onUsePlanButtonClick(view: View) {
        viewModelScope.launch {
            val plansCount = getPlansCountUseCase()
            if (plansCount == 0) {
                val snackbarText = view.resources.getString(R.string.scenario_no_plans_to_choose)
                Snackbar.make(view, snackbarText, Snackbar.LENGTH_SHORT).show()
            } else {
                val action = PowerWorkoutScenarioFragmentDirections.toPlanChoiceFragment()
                view.findNavController().navigate(action)
            }
        }
    }

    fun onDoNotUsePlanButtonClick() {
        uiState = UiState.SelectedNoPlan
    }

    fun onNextButtonClick(view: View) {
        viewModelScope.launch {
            savePreferences()
            val action = if (uiState == UiState.SelectedPlanUse) {
                setStage(WorkoutStage.WarmUp)
                startWorkoutService(view)
                PowerWorkoutScenarioFragmentDirections.toWorkoutFragment()
            } else {
                PowerWorkoutScenarioFragmentDirections.toWorkoutTypeChoiceFragment()
            }
            val navController = view.findNavController()
            navController.navigate(action)
        }
    }

    private suspend fun savePreferences() {
        if (uiState == UiState.SelectedPlanUse) {
            val prefs = SessionPrefs.WithPlan(plan = getPlan(chosenPlanId))
            setSessionPrefsUseCase(prefs)
        }
    }

    enum class UiState {
        NotSelected,
        SelectedPlanUse,
        SelectedNoPlan
    }

    override fun clear() {
        uiState = UiState.NotSelected
    }
}