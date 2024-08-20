package com.training.companion.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.training.companion.R
import com.training.companion.domain.models.Reps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExerciseRepsViewModel(
    previouslyChosenReps: Reps?,
    private val defaultExactRepsNumber: Reps.Exact,
) : ViewModel() {

    var currentReps = previouslyChosenReps ?: defaultExactRepsNumber
        private set(value) {
            field = value
            updateUiState(reps = field)
        }

    private val isStateCorrect: Boolean
        get() {
            return when (currentReps) {
                is Reps.Range -> {
                    val repsRange = currentReps as Reps.Range
                    val from = repsRange.value.first
                    val to = repsRange.value.last
                    from < to && from != 0
                }

                else -> true
            }
        }

    private val initUiState = when (val reps = currentReps) {
        is Reps.Exact -> {
            UiState(
                repsAsExact = reps.value,
                exactContainerIsVisible = true,
                removeButtonIsEnabled = previouslyChosenReps != null
            )
        }

        Reps.Max -> {
            UiState(
                maxContainerIsVisible = true, removeButtonIsEnabled = previouslyChosenReps != null
            )
        }

        is Reps.Range -> {
            UiState(
                repsAsRangeStart = reps.value.first.toString(),
                repsAsRangeEnd = reps.value.last.toString(),
                exactContainerIsVisible = true,
                removeButtonIsEnabled = previouslyChosenReps != null
            )
        }
    }

    private val _uiState = MutableStateFlow(initUiState)
    val uiState = _uiState.asStateFlow()

    //TODO: check impact
    val checkedButtonId = when (currentReps) {
        is Reps.Exact -> R.id.button_exact
        is Reps.Max -> R.id.button_max
        is Reps.Range -> R.id.button_range
    }

    fun onRepsExactChanged(value: Int) {
        currentReps = Reps.Exact(value)
    }

    fun onRepsFormatChangeButtonClick(checkedId: Int, isChecked: Boolean) {
        if (!isChecked) {
            return
        }
        when (checkedId) {
            R.id.button_exact -> currentReps = defaultExactRepsNumber
            R.id.button_range -> currentReps = Reps.Range(0..0)
            R.id.button_max -> currentReps = Reps.Max
        }
    }

    fun onRepsRangeChanged(from: CharSequence, to: CharSequence) {
        val repsFromIntValue = from.toString().toIntOrNull() ?: 0
        val repsToIntValue = to.toString().toIntOrNull() ?: 0
        currentReps = Reps.Range(repsFromIntValue..repsToIntValue)
    }

    private val rangeStartValue: Int?
        get() {
            val range = currentReps as? Reps.Range
            val fromValue = range?.value?.first
            return if (fromValue == 0) {
                null
            } else {
                fromValue
            }
        }

    private val rangeEndValue: Int?
        get() {
            val range = currentReps as? Reps.Range
            val toValue = range?.value?.last
            return if (toValue == 0) {
                null
            } else {
                toValue
            }
        }

    private fun updateUiState(reps: Reps) {
        var updatedState = uiState.value.copy(
            exactContainerIsVisible = false,
            rangeContainerIsVisible = false,
            maxContainerIsVisible = false
        )
        when (reps) {
            is Reps.Exact -> updatedState = updatedState.copy(
                repsAsExact = reps.value,
                exactContainerIsVisible = true,
                submitButtonIsEnabled = isStateCorrect,
            )

            Reps.Max -> updatedState = updatedState.copy(
                maxContainerIsVisible = true,
                submitButtonIsEnabled = isStateCorrect,
            )

            is Reps.Range -> updatedState = updatedState.copy(
                repsAsRangeStart = rangeStartValue?.toString(),
                repsAsRangeEnd = rangeEndValue?.toString(),
                rangeContainerIsVisible = true,
                submitButtonIsEnabled = isStateCorrect
            )
        }
        _uiState.update { updatedState }
    }

    data class UiState(
        val repsAsExact: Int? = null,
        val repsAsRangeStart: String? = null,
        val repsAsRangeEnd: String? = null,
        val exactContainerIsVisible: Boolean = false,
        val rangeContainerIsVisible: Boolean = false,
        val maxContainerIsVisible: Boolean = false,
        val submitButtonIsEnabled: Boolean = true,
        val removeButtonIsEnabled: Boolean = false,
    )
}