package com.training.companion.presentation.viewmodels

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.training.companion.R
import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.PlanEditType
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.usecases.DeletePlanUseCase
import com.training.companion.domain.usecases.DuplicatePlanUseCase
import com.training.companion.domain.usecases.GetPlanUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.fragments.PlanDetailsFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.util.startWorkoutService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlanDetailsViewModel(
    private val planId: Int,
    private val getPlan: GetPlanUseCase,
    private val duplicatePlan: DuplicatePlanUseCase,
    private val deletePlan: DeletePlanUseCase,
    private val setStage: SetStageUseCase,
    private val setSessionPrefs: SetSessionPrefsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val plan = fetchPlan()
            _uiState.update {
                it.copy(
                    plan = plan,
                    fetchFinished = true,
                    commentIsEmpty = plan.comment.isNullOrBlank()
                )
            }
        }
    }

    fun onCommentClick(view: View) {
        val textField = view as TextInputEditText
        if (textField.lineCount > 6) {
            _uiState.update { it.copy(commentIsExpanded = !it.commentIsExpanded) }
        }
    }

    fun handleOnBackPress(view: View) {
        if (uiState.value.commentIsExpanded) {
            _uiState.update { it.copy(commentIsExpanded = false) }
        } else {
            navigateBack(view)
        }
    }

    fun onNavigationBackClick(view: View) {
        navigateBack(view)
    }

    fun onMenuItemClick(menuItem: MenuItem, view: View): Boolean {
        when (menuItem.itemId) {
            R.id.edit_plan -> {
                navigateToEditFragment(view, planId, PlanEditType.Editing)
            }

            R.id.start_workout -> {
                setSessionPrefs(SessionPrefs.WithPlan(uiState.value.plan!!))
                setStage(WorkoutStage.WarmUp)
                startWorkoutService(view)
                navigateToWorkout(view)
            }

            R.id.duplicate_plan -> {
                viewModelScope.launch {
                    val duplicateId = duplicatePlan(view.context, planId)
                    navigateToEditFragment(view, duplicateId, PlanEditType.Duplicate)
                }
            }

            R.id.delete_plan -> {
                showDeletePlanConfirmDialog(view.context) {
                    viewModelScope.launch {
                        deletePlan(planId)
                        navigateBack(view)
                    }
                }
            }
            else -> return false
        }
        return true
    }

    private fun navigateBack(view: View) {
        val navController = view.findNavController()
        navController.popBackStack()
    }

    private fun navigateToEditFragment(view: View, planId: Int, planEditType: PlanEditType) {
        val navController = view.findNavController()
        val action = PlanDetailsFragmentDirections.toPlanEditFragment(
            SetDetailsBuilder.OfPlanEdit(
                workoutType = uiState.value.plan!!.workoutType,
                planEditType = planEditType,
                planId = planId
            )
        )
        navController.navigate(action)
    }

    private fun navigateToWorkout(view: View) {
        val navController = view.findNavController()
        PlanDetailsFragmentDirections.toWorkoutFragment().also {
            navController.navigate(it)
        }
    }

    private suspend fun fetchPlan(): WorkoutPlan {
        return getPlan.invoke(planId)
    }

    private fun showDeletePlanConfirmDialog(context: Context, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.plan_delete_confirm_title)
            .setMessage(R.string.plan_delete_confirm_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                onConfirm.invoke()
            }.setNegativeButton(R.string.cancel, null)
            .show()
    }

    data class UiState(
        val fetchFinished: Boolean = false,
        val plan: WorkoutPlan? = null,
        val commentIsExpanded: Boolean = false,
        val commentIsEmpty: Boolean = false,
    )

    companion object {
        @JvmStatic
        @BindingAdapter("planDetails_exercisesFullScreen")
        fun showExercisesFullScreen(recyclerView: RecyclerView, show: Boolean) {
            if (show) {
                recyclerView.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                ).apply {
                    val margin = recyclerView.resources
                        .getDimension(R.dimen.plan_exercise_margin).toInt()
                    topToBottom = R.id.toolbar
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    setMargins(margin, 0, margin, 0)
                }
            }
        }
    }
}