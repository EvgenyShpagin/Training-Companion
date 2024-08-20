package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.navigation.findNavController
import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.WorkoutPlace
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.usecases.GetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.presentation.fragments.WorkoutTypeChoiceFragmentDirections
import com.training.companion.presentation.viewmodels.base.StartingWorkoutBaseViewModel

class TrainingTypeViewModel(
    getSessionPrefsUseCase: GetSessionPrefsUseCase,
    setSessionPrefsUseCase: SetSessionPrefsUseCase
) : StartingWorkoutBaseViewModel(getSessionPrefsUseCase, setSessionPrefsUseCase) {

    fun onTypeSelect(view: View, type: WorkoutType) {
        val sessionPrefs = SessionPrefs.WithoutPlan(
            type = type,
            restTime = null,
            place = WorkoutPlace.None
        )
        setSessionPrefsUseCase(sessionPrefs)
        navigate(view)
    }

    fun navigateBack(view: View) {
        view.findNavController().popBackStack()
    }

    private fun navigate(view: View) {
        WorkoutTypeChoiceFragmentDirections.toPowerWorkoutPlaceFragment().also {
            view.findNavController().navigate(it)
        }
    }

    override fun clear() {}
}