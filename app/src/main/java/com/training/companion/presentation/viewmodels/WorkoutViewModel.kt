package com.training.companion.presentation.viewmodels

import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.R
import com.training.companion.domain.enums.Action
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.models.Set
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.GetAvailableActionsUseCase
import com.training.companion.domain.usecases.GetCompletedSetsUseCase
import com.training.companion.domain.usecases.GetCurrentPlanSetUseCase
import com.training.companion.domain.usecases.GetCurrentStageUseCase
import com.training.companion.domain.usecases.GetLastCompletedSetUseCase
import com.training.companion.domain.usecases.GetNextSetUseCase
import com.training.companion.domain.usecases.GetStagePreferencesUseCase
import com.training.companion.domain.usecases.GetStageTimeUseCase
import com.training.companion.domain.usecases.GetStageUseCase
import com.training.companion.domain.usecases.GetTotalTimeUseCase
import com.training.companion.domain.usecases.IsSetCommitNotFinished
import com.training.companion.domain.usecases.IsWorkoutInProgress
import com.training.companion.domain.usecases.RestartStageUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.dialogs.FinishConfirmDialog
import com.training.companion.presentation.fragments.toplevel.WorkoutFragmentDirections
import com.training.companion.presentation.util.stopWorkoutService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val setStage: SetStageUseCase,
    private val getTotalTimeUseCase: GetTotalTimeUseCase,
    private val getStageTimeUseCase: GetStageTimeUseCase,
    private val getStageUseCase: GetStageUseCase,
    private val getStagePreferencesUseCase: GetStagePreferencesUseCase,
    private val isSetCommitNotFinished: IsSetCommitNotFinished,
    private val getNextPlanSet: GetNextSetUseCase,
    private val getAvailableActions: GetAvailableActionsUseCase,
    private val getCompletedSets: GetCompletedSetsUseCase,
    private val getLastCompletedSet: GetLastCompletedSetUseCase,
    private val getCurrentPlanSet: GetCurrentPlanSetUseCase,
    private val restartStage: RestartStageUseCase,
    val isWorkoutInProgress: IsWorkoutInProgress,
    getCurrentStage: GetCurrentStageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(currentStage = getCurrentStage()))
    val uiState = _uiState.asStateFlow()

    private val _totalTime = MutableStateFlow(Time.ZERO)
    val totalTime = _totalTime.asStateFlow()

    private val _stageTime = MutableStateFlow(Time.ZERO)
    val stageTime = _stageTime.asStateFlow()

    private val collectorJobs = arrayListOf<Job>()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            launch {
                getTotalTimeUseCase().collectLatest { totalTime ->
                    _totalTime.update { totalTime }
                }
            }.also { collectorJobs.add(it) }

            launch {
                getStageTimeUseCase().collectLatest { stageTime ->
                    _stageTime.update { stageTime }
                }
            }.also { collectorJobs.add(it) }

            launch {
                getStageUseCase().collectLatest { stage ->
                    onStageChange(stage)
                }
            }.also { collectorJobs.add(it) }

            launch {
                getLastCompletedSet().collectLatest { lastSet ->
                    updateCompletedSets(lastSet)
                }
            }.also { collectorJobs.add(it) }
        }

        _uiState.update {
            it.copy(
                lastCompletedSets = getCompletedSets().takeLast(2),
                availableActions = getAvailableActions()
            )
        }
    }

    private fun updateCompletedSets(lastSet: CompletedSet?) {
        val currentSets = uiState.value.lastCompletedSets
        if (lastSet == null || currentSets.contains(lastSet)) return
        _uiState.update {
            it.copy(
                lastCompletedSets = when (currentSets.count()) {
                    0 -> listOf(lastSet)

                    1 -> if (currentSets[0].ordinal == lastSet.ordinal) {
                        listOf(lastSet)
                    } else {
                        currentSets + lastSet
                    }

                    2 -> if (currentSets[1].ordinal == lastSet.ordinal) {
                        listOf(currentSets[0], lastSet)
                    } else {
                        listOf(currentSets[1], lastSet)
                    }

                    else -> throw IllegalStateException()
                }
            )
        }
    }

    private fun onStageChange(stage: WorkoutStage) {
        if (stage == WorkoutStage.Finished) {
            cancelCollectors()
            return
        }
        if (stage != WorkoutStage.Suspense) {
            val stagePreferences = getStagePreferencesUseCase(stage)

            var currentPlanSet: Set? = null
            var nextPlanSet: Set? = null

            when (stage) {
                WorkoutStage.WarmUp -> nextPlanSet = getNextPlanSet()
                WorkoutStage.Rest -> nextPlanSet = getNextPlanSet()
                WorkoutStage.Exercise -> currentPlanSet = getCurrentPlanSet()
                else -> {}
            }

            _uiState.update { state ->
                state.copy(
                    currentSet = currentPlanSet,
                    nextSet = nextPlanSet,
                    stageInitTime = stagePreferences.initTime,
                    currentStage = stage,
                    availableActions = getAvailableActions()
                )
            }
        }
    }

    fun onMenuItemClick(view: View, menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.pause_workout) {
            viewModelScope.launch {
                setStage.invoke(WorkoutStage.Suspense)
                val action = WorkoutFragmentDirections.toSuspenseFragment()
                view.findNavController().navigate(action)
            }
        }
        return true
    }

    fun onFinishClick(view: View) {
        FinishConfirmDialog(view.context).show { _, _ ->
            stopWorkoutService(view)
            val action = WorkoutFragmentDirections.toResultsFragment()
            view.findNavController().navigate(action)
        }
    }

    fun onBackPress(view: View) {
        onFinishClick(view)
    }

    fun getStageNameResourceId(stage: WorkoutStage): Int {
        return when (stage) {
            WorkoutStage.WarmUp -> R.string.workout_stage_warm_up
            WorkoutStage.Exercise -> R.string.workout_stage_exercise
            WorkoutStage.Rest -> R.string.workout_stage_rest
            WorkoutStage.Stretching -> R.string.workout_stage_stretching
            WorkoutStage.Suspense -> R.string.workout_stage_suspense
            WorkoutStage.Finished -> R.string.workout_stage_finish
        }
    }

    fun onFinishStageClick(view: View) {
        viewModelScope.launch {
            when (uiState.value.currentStage) {
                WorkoutStage.WarmUp -> {
                    setStage.invoke(WorkoutStage.Exercise)
                }

                WorkoutStage.Exercise -> {
                    proceedFromExerciseToRest(view)
                }

                WorkoutStage.Rest -> {
                    setStage.invoke(WorkoutStage.Exercise)
                }

                else -> return@launch
            }
        }
    }

    fun onActionClick(view: View, action: Action) {
        viewModelScope.launch {
            when (action) {
                Action.GO_TO_REST -> {
                    if (uiState.value.currentStage == WorkoutStage.Exercise) {
                        proceedFromExerciseToRest(view)
                    } else {
                        setStage(WorkoutStage.Rest)
                    }
                }

                Action.START_EXERCISE -> setStage.invoke(WorkoutStage.Exercise)


                Action.ADD_EXTRA_STOPWATCH -> {
                    //TODO: in future
                }

                Action.CHANGE_REST_TIME -> {
                    //TODO: soon
                }

                Action.RETURN_TO_PREVIOUS_STAGE -> {
                    //TODO: soon
                }

                Action.RESTART_SET -> restartStage()
            }
        }
    }

    private suspend fun proceedFromExerciseToRest(view: View) {
        setStage(WorkoutStage.Rest)
        navigateToStageSetCompletion(view)
    }

    private fun navigateToStageSetCompletion(view: View) {
        val action = WorkoutFragmentDirections.toSetCompletionFragment()
        view.findNavController().navigate(action)
    }

    private fun cancelCollectors() {
        collectorJobs.forEach { it.cancel() }
        collectorJobs.clear()
    }

    data class UiState(
        val currentStage: WorkoutStage,
        val stageInitTime: Time = Time.ZERO,
        val currentSet: Set? = null,
        val nextSet: Set? = null,
        val lastCompletedSets: List<CompletedSet> = emptyList(),
        val availableActions: List<Action> = emptyList(),
    )
}