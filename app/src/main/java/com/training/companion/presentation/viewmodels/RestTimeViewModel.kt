package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.BR
import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.StagePreferences
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.TimeCountingMethod
import com.training.companion.domain.usecases.GetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetStagePreferencesUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.fragments.PowerWorkoutRestTimeFragmentDirections
import com.training.companion.presentation.viewmodels.base.StartingWorkoutBaseViewModel
import com.training.companion.presentation.util.startWorkoutService
import kotlinx.coroutines.launch

class RestTimeViewModel(
    private val setStagePreferencesUseCase: SetStagePreferencesUseCase,
    getSessionPrefsUseCase: GetSessionPrefsUseCase,
    setSessionPrefsUseCase: SetSessionPrefsUseCase,
    private val setStage: SetStageUseCase
) : StartingWorkoutBaseViewModel(getSessionPrefsUseCase, setSessionPrefsUseCase) {

    var currentTime = Time(180)

    @Bindable
    var noCountdownRestTime = false
        set(value) {
            field = value
            checkButtonEnable()
            notifyPropertyChanged(BR.noCountdownRestTime)
        }

    @Bindable
    var buttonEnabled = true
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.buttonEnabled)
            }
        }

    fun navigateBack(view: View) {
        view.findNavController().popBackStack()
    }

    fun onStartClick(view: View) {
        viewModelScope.launch {
            saveTime()
            setStage(WorkoutStage.WarmUp)
            startWorkoutService(view)
            val action = PowerWorkoutRestTimeFragmentDirections.toWorkoutFragment()
            view.findNavController().navigate(action)
        }
    }

    fun onSecondsChange(toValue: Int) {
        currentTime = currentTime.copy(seconds = toValue)
        clearCheckbox()
        checkButtonEnable()
    }

    fun onMinutesChange(toValue: Int) {
        currentTime = currentTime.copy(minutes = toValue)
        clearCheckbox()
        checkButtonEnable()
    }

    override fun clear() {}

    private fun saveTime() {
        val oldSessionPrefs = getSessionPrefsUseCase() as SessionPrefs.WithoutPlan
        val stagePreferences: StagePreferences
        if (noCountdownRestTime) {
            stagePreferences = StagePreferences(TimeCountingMethod.Stopwatch, Time.ZERO)
            setSessionPrefsUseCase(oldSessionPrefs.copy(restTime = null))
        } else {
            stagePreferences = StagePreferences(TimeCountingMethod.Countdown, currentTime)
            setSessionPrefsUseCase(oldSessionPrefs.copy(restTime = currentTime))
        }
        setStagePreferencesUseCase(WorkoutStage.Rest, stagePreferences)
    }

    private fun checkButtonEnable() {
        buttonEnabled = currentTime.totalSeconds != 0 || noCountdownRestTime
    }

    private fun clearCheckbox() {
        if (noCountdownRestTime) {
            noCountdownRestTime = false
        }
    }
}