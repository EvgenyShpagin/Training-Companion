package com.training.companion.presentation.viewmodels

import androidx.databinding.Bindable
import com.google.android.material.textfield.TextInputLayout
import com.training.companion.BR
import com.training.companion.R
import com.training.companion.data.repositories.AppSettings
import com.training.companion.domain.models.Weight
import com.training.companion.domain.util.Constraints
import com.training.companion.presentation.util.decimalFormat0_0
import com.training.companion.presentation.viewmodels.base.ObservableViewModel


class WeightExerciseViewModel(val previouslyChosenWeight: Weight?) : ObservableViewModel() {

    private var weightValue = previouslyChosenWeight?.value ?: 0.0

    private val weightIsChosen get() = weightValue != 0.0

    private val correctInputRegex = "^(([1-9][0-9]{0,2})|([0-9]{0,3}(((\\.)|(,))[0-9]{0,3}))?)$".toRegex()

    val weightUnits = AppSettings.units.weightUnit

    @Bindable
    var inputTextIsCorrect = previouslyChosenWeight != null
        private set(value) {
            if (field == value) return
            field = value
            notifyPropertyChanged(BR.inputTextIsCorrect)
        }

    fun getWeight() = if (weightIsChosen) {
        Weight(weightValue, weightUnits)
    } else {
        null
    }

    fun onTextChanged(text: CharSequence, inputLayout: TextInputLayout) {

        if (text.isEmpty()) {
            removeError(inputLayout)
            return
        }

        val textHasCorrectFormat = text.matches(correctInputRegex)
        val value = text.toString().toDoubleOrNull()

        val weightWithinLimits = value?.let { isWeightWithinLimits(it) }

        if (!textHasCorrectFormat || value == null || !weightWithinLimits!!) {
            inputTextIsCorrect = false
            setError(inputLayout)
        } else {
            inputTextIsCorrect = true
            removeError(inputLayout)
            onWeightChanged(value)
        }
    }

    fun getPreviousWeightAsText(): String? {
        return previouslyChosenWeight?.let { decimalFormat0_0.format(it.value) }
    }

    private fun isWeightWithinLimits(value: Double): Boolean {
        val currentWeight = Weight(value, weightUnits)
        val weightKilos = currentWeight.kgs
        return weightKilos > 0.0 && weightKilos <= Constraints.MAX_WEIGHT_KG
    }
    private fun setError(inputLayout: TextInputLayout) {
        val context = inputLayout.context
        inputLayout.error = context.getString(R.string.wrong_weight_input_format)
    }

    private fun removeError(inputLayout: TextInputLayout) {
        inputLayout.error = null
    }

    private fun onWeightChanged(value: Double) {
        weightValue = value
    }

    override fun clear() {}

}