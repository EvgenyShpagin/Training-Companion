package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.databinding.Bindable
import androidx.navigation.findNavController
import com.training.companion.BR
import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.WorkoutPlace
import com.training.companion.domain.usecases.GetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.presentation.fragments.PowerWorkoutPlaceFragmentDirections
import com.training.companion.presentation.viewmodels.base.StartingWorkoutBaseViewModel

class PlaceViewModel(
    getSessionPrefsUseCase: GetSessionPrefsUseCase,
    setSessionPrefsUseCase: SetSessionPrefsUseCase
) : StartingWorkoutBaseViewModel(getSessionPrefsUseCase, setSessionPrefsUseCase) {

    @Bindable
    var selectedPlace = WorkoutPlace.None
        set(value) {
            field = value
            notifyPropertyChanged(BR.placeSelected)
            notifyPropertyChanged(BR.selectedPlace)
        }

    @get:Bindable
    val isPlaceSelected
        get() = selectedPlace != WorkoutPlace.None

    fun onSelect(place: WorkoutPlace) {
        selectedPlace = place
    }

    fun navigateBack(view: View) {
        view.findNavController().popBackStack()
    }

    fun onNextClick(view: View) {
        savePlace()
        val action = PowerWorkoutPlaceFragmentDirections.toPowerWorkoutRestTimeFragment()
        view.findNavController().navigate(action)
    }

    override fun clear() {
        selectedPlace = WorkoutPlace.None
    }

    private fun savePlace() {
        val oldSessionPrefs = getSessionPrefsUseCase() as SessionPrefs.WithoutPlan
        val updatedSessionPrefs = oldSessionPrefs.copy(place = selectedPlace)
        setSessionPrefsUseCase(updatedSessionPrefs)
    }
}