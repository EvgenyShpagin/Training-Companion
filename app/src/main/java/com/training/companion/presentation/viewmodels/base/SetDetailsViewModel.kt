package com.training.companion.presentation.viewmodels.base

import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.training.companion.R
import com.training.companion.data.repositories.AppSettings
import com.training.companion.domain.enums.WeightUnit
import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.models.Reps
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.Weight
import com.training.companion.domain.usecases.GetExerciseExtrasUseCase
import com.training.companion.presentation.models.SetDetailsBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

abstract class SetDetailsViewModel(
    val setBuilder: SetDetailsBuilder,
    protected val getExerciseExtrasUseCase: GetExerciseExtrasUseCase,
) : ViewModel() {

    var setsNumber: Int? = null
        protected set
    var reps: Reps? = null
        protected set
    var restTime: Time? = null
        protected set
    var duration: Time? = null
        protected set
    var weight: Weight? = null
        protected set

    protected val requiredWeightUnits = AppSettings.units.weightUnit

    protected val _setState = MutableStateFlow(
        ItemState(
            iconRes = R.drawable.ic_sets_24,
            titleRes = R.string.title_sets,
            helperTextRes = R.string.helper_text_plan_exercise_sets,
            onClickListener = { showDialog(it, R.id.to_exerciseSetsBottomSheet) }
        )
    )
    val setState = _setState.asStateFlow()

    protected val _restTimeState = MutableStateFlow(
        ItemState(
            iconRes = R.drawable.ic_rest_time_24,
            titleRes = R.string.title_rest,
            helperTextRes = R.string.helper_text_plan_exercise_rest_not_selected,
            onClickListener = { showDialog(it, R.id.to_exerciseRestTimeBottomSheet) }
        )
    )
    val restTimeState = _restTimeState.asStateFlow()

    protected val _repsState = MutableStateFlow(
        ItemState(
            iconRes = R.drawable.ic_reps_24,
            titleRes = R.string.title_reps,
            helperTextRes = R.string.helper_text_plan_exercise_reps_not_selected,
            onClickListener = { showDialog(it, R.id.to_exerciseRepsPlanEditBottomSheet) }
        )
    )
    val repsState = _repsState.asStateFlow()

    protected val _durationState = MutableStateFlow(
        ItemState(
            iconRes = R.drawable.ic_duration_24,
            titleRes = R.string.title_duration,
            helperTextRes = R.string.helper_text_plan_exercise_duration_not_selected,
            onClickListener = { showDialog(it, R.id.to_exerciseDurationBottomSheet) }
        )
    )
    val durationState = _durationState.asStateFlow()

    protected val _weightState = MutableStateFlow(
        ItemState(
            iconRes = R.drawable.ic_weight_24,
            titleRes = R.string.title_weight,
            helperTextRes = R.string.helper_text_plan_exercise_weight,
            onClickListener = { showDialog(it, R.id.to_exerciseWeightBottomSheet) },
            stringWrapperId = when (requiredWeightUnits) {
                WeightUnit.Kilograms -> R.string.weight_kg_suffix
                WeightUnit.Pounds -> R.string.weight_lb_suffix
            }
        )
    )
    val weightState = _weightState.asStateFlow()

    protected val _allowedToNavigateNext = MutableStateFlow(false)
    val allowedToNavigateNext = _allowedToNavigateNext.asStateFlow()

    abstract fun onSubmitClick(view: View)

    open fun onBackPressed(view: View) {
        view.findNavController().popBackStack()
    }

    open fun onSetsNumberChosen(count: Int) {
        setsNumber = count
        _setState.update { it.copy(value = count.toString(), isChosen = true) }
    }

    open fun onRestTimeChosen(time: Time?) {
        _restTimeState.update {
            it.copy(value = timeToString(time), isChosen = time != null)
        }
        restTime = time
    }

    open fun onRepsChosen(reps: Reps?) {
        _repsState.update {
            it.copy(value = reps?.toString(), isChosen = reps != null)
        }
        this.reps = reps
    }

    open fun onWeightChosen(weight: Weight?) {
        _weightState.updateAndGet {
            it.copy(value = weightToString(weight), isChosen = weight != null)
        }.also { Log.d("TAG_1", "onDurationChosen: value = ${it.value}") }
        this.weight = weight
    }

    open fun onDurationChosen(duration: Time?) {
        _durationState.update {
            it.copy(value = timeToString(duration), isChosen = duration != null)
        }
        this.duration = duration
    }

    protected fun weightToString(weight: Weight? = this.weight): String? {
        return weight?.toStringRoundedTo100s()
    }

    protected fun timeToString(time: Time?): String? {
        return time?.toString(Time.Format.MM_SS)
    }

    protected fun showDialog(view: View, resId: Int) {
        val navController = view.findNavController()
        navController.navigate(resId)
    }

    protected suspend fun getExerciseExtras(exerciseId: Int): ExerciseExtras {
        return getExerciseExtrasUseCase(exerciseId)
    }

    data class ItemState(
        val isVisible: Boolean = false,
        val isChosen: Boolean = false,
        val value: String? = null,
        @DrawableRes val iconRes: Int? = null,
        @StringRes val titleRes: Int? = null,
        @StringRes val helperTextRes: Int? = null,
        @StringRes val stringWrapperId: Int? = null,
        val onClickListener: View.OnClickListener? = null,
    )
}