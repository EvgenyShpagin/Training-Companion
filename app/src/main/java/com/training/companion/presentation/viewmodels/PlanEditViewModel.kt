package com.training.companion.presentation.viewmodels

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.training.companion.BR
import com.training.companion.R
import com.training.companion.domain.enums.PlanEditType
import com.training.companion.domain.enums.PlanExerciseListState
import com.training.companion.domain.enums.PlanNameCheckResult
import com.training.companion.domain.models.PlanExercise
import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.usecases.CheckPlanNameUseCase
import com.training.companion.domain.usecases.DeletePlanUseCase
import com.training.companion.domain.usecases.FormatPlainTextUseCase
import com.training.companion.domain.usecases.GetPlanUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.presentation.fragments.PlanEditFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.base.ObservableViewModel
import com.training.companion.toNullablePlanExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PlanEditViewModel(
    private val setBuilder: SetDetailsBuilder.OfPlanEdit,
    private val savePlan: SavePlanUseCase,
    private val getPlan: GetPlanUseCase,
    private val deletePlanUseCase: DeletePlanUseCase,
    private val checkPlanName: CheckPlanNameUseCase,
    private val formatPlainText: FormatPlainTextUseCase,
) : ObservableViewModel() {

    private var plan: WorkoutPlan? = null
    private val _planExercises = MutableStateFlow<List<PlanExercise>>(emptyList())
    val planExercises = _planExercises.asStateFlow()
    private val removedPlanExercises = mutableListOf<PlanExercise>()

    private val _currentListState = MutableStateFlow(PlanExerciseListState.Default)
    val currentListState = _currentListState.asStateFlow()

    private val _menuSaveButtonEnabled = MutableStateFlow(false)
    val menuSaveButtonEnabled = _menuSaveButtonEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            plan = getPlan(setBuilder.planId)
            name = plan!!.name
            savedName = name
            comment = plan!!.comment ?: ""
            savedComment = comment
            _planExercises.update { plan!!.planExercises }
            exercisesAdded = _planExercises.value.isNotEmpty()
            _menuSaveButtonEnabled.update { exercisesAdded }
        }
    }

    val onExerciseClickListener = { view: View, exerciseOrdinal: Int ->
        when (currentListState.value) {
            PlanExerciseListState.Default -> {
                navigateToExerciseChoice(view, exerciseOrdinal)
            }

            PlanExerciseListState.Deleting -> {
                removeExercise(exerciseOrdinal)
            }
        }
    }

    @get:Bindable
    var exercisesAdded: Boolean = _planExercises.value.isNotEmpty()
        private set(value) {
            if (field == value) {
                return
            }
            field = value
            if (!value) {
                viewModelScope.launch {
                    _currentListState.update { PlanExerciseListState.Default }
                }
            }
            notifyPropertyChanged(BR.exercisesAdded)
        }

    private var name = ""
    private var savedName = ""

    private var comment = ""
    private var savedComment = ""

    fun onSavePlanClick(view: View): Boolean {
        showNameInputDialog(view)
        return true
    }

    fun handleOnBackPress(view: View) {
        breakEditing(view)
    }

    fun onNavigationBackClick(view: View) {
        breakEditing(view)
    }

    fun onAddButtonClick(view: View) {
        navigateToExerciseChoice(view, getOrdinalForNewExercise())
    }

    fun onCommentClick(view: View) {
        showCommentInputDialog(view.context)
    }

    fun onRemoveButtonClick() {
        _currentListState.update { PlanExerciseListState.Deleting }
        _menuSaveButtonEnabled.update { false }
    }

    fun onSubmitRemovingButtonClick() {
        _currentListState.update { PlanExerciseListState.Default }
        viewModelScope.launch {
            deleteExercises(removedPlanExercises)
            removedPlanExercises.clear()
            updateStateAfterRemove()
        }
    }

    fun onCancelRemovingButtonClick() {
        _currentListState.update { PlanExerciseListState.Default }
        _planExercises.update { getPlanExercisesWithRemoved() }
        _menuSaveButtonEnabled.update { true }
        removedPlanExercises.clear()
    }

    private fun finishPlanAndNavigateBack(navController: NavController, input: String) {
        viewModelScope.launch {
            name = formatPlainText.removeSpaces(input)
            saveName()
            fixExerciseOrdinals()
            savePlan.finishChanges(setBuilder.planId)
        }.invokeOnCompletion {
            navigateBack(navController)
        }
    }

    private fun inflateInputLayout(context: Context, layoutRes: Int): ViewGroup {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutRes, null) as ViewGroup
    }

    private fun getTextFieldLayout(parent: ViewGroup): TextInputLayout {
        return parent.findViewById(R.id.text_field_layout)!!
    }

    private fun getTextField(textLayout: TextInputLayout): TextInputEditText {
        return textLayout.findViewById(R.id.text_field)
    }

    private fun showCommentInputDialog(context: Context) {
        val layoutOfTextInput = getLayoutOfTextInputForComment(context)
        val textFieldLayout = getTextFieldLayout(layoutOfTextInput)
        val textField = getTextField(textFieldLayout)
        textField.setText(comment)

        MaterialAlertDialogBuilder(context)
            .setMessage(R.string.plan_dialog_input_comment_message)
            .setView(layoutOfTextInput)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.submit) { _, _ ->
                viewModelScope.launch {
                    comment = textField.editableText.toString()
                    saveComment()
                }
            }.show()
    }

    private fun showNameInputDialog(view: View) {
        val layoutOfTextInput = getLayoutOfTextInputForName(view.context)
        val textFieldLayout = getTextFieldLayout(layoutOfTextInput)
        val textField = getTextField(textFieldLayout)
        textField.setText(name)

        val dialog = MaterialAlertDialogBuilder(view.context)
            .setMessage(R.string.plan_dialog_input_name_message)
            .setView(layoutOfTextInput)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = textField.editableText!!.toString()
                val navController = view.findNavController()
                finishPlanAndNavigateBack(navController, name)
            }.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            handleInputName(name, textFieldLayout, saveButton)
            textField.doAfterTextChanged {
                handleInputName(it?.toString(), textFieldLayout, saveButton)
            }
        }
        dialog.show()
    }

    private fun handleInputName(
        currentName: String?,
        inputLayout: TextInputLayout,
        button: Button,
    ) {
        val context = inputLayout.context
        val formattedName = currentName?.let { formatPlainText.removeSpaces(it) }
        viewModelScope.launch {
            when (checkPlanName.invoke(formattedName)) {
                PlanNameCheckResult.CORRECT -> {
                    inputLayout.error = null
                    inputLayout.isErrorEnabled = false
                    button.isEnabled = true
                }

                PlanNameCheckResult.ALREADY_EXISTS -> {
                    if (savedName != name) {
                        inputLayout.error = context.getString(R.string.plan_name_already_exists)
                        button.isEnabled = false
                    }
                }

                PlanNameCheckResult.INCLUDES_PROHIBITED_CHARS -> {
                    inputLayout.error = context.getString(R.string.plan_name_contains_prohibited)
                    button.isEnabled = false
                }

                PlanNameCheckResult.EMPTY -> {
                    inputLayout.error = null
                    inputLayout.isErrorEnabled = false
                    button.isEnabled = false
                }
            }
        }
    }

    private fun getLayoutOfTextInputForComment(context: Context) =
        inflateInputLayout(context, R.layout.edit_plan_comment_text_field)

    private fun getLayoutOfTextInputForName(context: Context) =
        inflateInputLayout(context, R.layout.edit_plan_name_text_field)

    private fun getPlanExercisesWithoutRemovedOne(ordinal: Int): List<PlanExercise> {
        return List(size = _planExercises.value.size - 1) { i ->
            val indexOutOfRemoved = if (i >= ordinal) {
                i + 1
            } else {
                i
            }
            _planExercises.value[indexOutOfRemoved]
        }
    }

    private fun getPlanExercisesWithRemoved(): List<PlanExercise> {
        return (planExercises.value + removedPlanExercises).sortedBy { it.ordinal }
    }

    private fun breakEditing(view: View) {
        if (!exercisesAdded || setBuilder.planEditType == PlanEditType.Duplicate) {
            deletePlan() // delete empty plan
        }
        navigateBack(view)
    }

    private fun navigateToExerciseChoice(view: View, exerciseOrdinal: Int) {
        val action = PlanEditFragmentDirections.toExerciseChoiceFragment(
            setBuilder.copy(exerciseOrdinal = exerciseOrdinal, workoutType = plan!!.workoutType)
        )
        view.findNavController().navigate(action)
    }

    private fun navigateBack(view: View) {
        val navController = view.findNavController()
        navigateBack(navController)
    }

    private fun navigateBack(navController: NavController) {
        navController.popBackStack()
    }

    private suspend fun deleteExercises(exercises: Collection<PlanExercise>) {
        exercises.forEach {
            val deleteUpdate = PlanUpdates(setBuilder.planId, deletePlanExerciseById = it.id)
            savePlan.saveChanges(deleteUpdate)
        }
    }

    private fun deletePlan() {
        removedPlanExercises.clear()
        viewModelScope.launch {
            deletePlanUseCase.invoke(plan!!.id)
        }
    }

    private fun getOrdinalForNewExercise(): Int {
        val planExercises = planExercises.value
        return if (planExercises.isNotEmpty()) {
            return planExercises.last().ordinal + 1
        } else {
            0
        }
    }

    private fun updateStateAfterRemove() {
        updateExerciseCountState()
        _menuSaveButtonEnabled.update { exercisesAdded }
    }

    private fun updateExerciseCountState() {
        exercisesAdded = planExercises.value.isNotEmpty()
    }

    private fun removeExercise(ordinal: Int) {
        val itemToRemove = planExercises.value[ordinal]
        removedPlanExercises.add(itemToRemove)
        _planExercises.update { getPlanExercisesWithoutRemovedOne(ordinal) }
    }

    private suspend fun saveName() {
        if (name != savedName) {
            savePlan.saveChanges(PlanUpdates(setBuilder.planId, newName = name))
        }
    }

    private suspend fun saveComment() {
        if (comment != savedComment) {
            savedComment = comment
            savePlan.saveChanges(PlanUpdates(setBuilder.planId, newComment = comment))
        }
    }

    private suspend fun fixExerciseOrdinals() {
        doForEveryExerciseInWrongOrder { exercise, correctOrdinal ->
            val fixedPlanExercise = exercise.copy(ordinal = correctOrdinal)
            val ordinalUpdate = PlanUpdates(
                planId = setBuilder.planId,
                updatedPlanExercise = fixedPlanExercise.toNullablePlanExercise()
            )
            savePlan.saveChanges(planUpdates = ordinalUpdate)
        }
    }

    private suspend fun doForEveryExerciseInWrongOrder(
        action: suspend (exercise: PlanExercise, correctOrdinal: Int) -> Unit,
    ) {
        var correctOrdinal = 0
        val exercises = planExercises.value
        exercises.forEach {
            if (it.ordinal != correctOrdinal) {
                action.invoke(it, correctOrdinal)
            }
            ++correctOrdinal
        }
    }

    override fun clear() {}

}