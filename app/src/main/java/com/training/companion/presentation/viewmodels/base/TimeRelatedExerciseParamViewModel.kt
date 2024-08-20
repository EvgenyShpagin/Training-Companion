package com.training.companion.presentation.viewmodels.base

import androidx.databinding.Bindable
import com.training.companion.BR
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.CheckTimeIsCorrectUseCase


open class TimeRelatedExerciseParamViewModel(
    val previouslyChosenTime: Time?,
    private val checkTimeIsCorrect: CheckTimeIsCorrectUseCase,
    defaultTime: Time,
) : ObservableViewModel() {

    @get:Bindable
    var chosenTime: Time = previouslyChosenTime ?: defaultTime

    @Bindable
    var timeIsCorrect: Boolean = checkTimeIsCorrect(chosenTime)
        set(value) {
            val prevValue = field
            field = value
            if (field != prevValue) {
                notifyPropertyChanged(BR.timeIsCorrect)
            }
        }

    fun onMinutesChanged(minutes: Int) {
        chosenTime = chosenTime.copy(minutes = minutes)
        timeIsCorrect = checkTimeIsCorrect(chosenTime)
    }

    fun onSecondsChanged(seconds: Int) {
        chosenTime = chosenTime.copy(seconds = seconds)
        timeIsCorrect = checkTimeIsCorrect(chosenTime)
    }

    override fun clear() {}
}