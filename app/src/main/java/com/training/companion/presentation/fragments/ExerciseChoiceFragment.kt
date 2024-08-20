package com.training.companion.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.training.companion.R
import com.training.companion.databinding.FragmentExerciseChoiceBinding
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.recyclerview.adapters.ExerciseSelectionAdapter
import com.training.companion.presentation.viewmodels.base.ExerciseChoiceViewModel
import com.training.companion.presentation.viewmodels.factories.ExerciseChoicePlanEditFactory
import com.training.companion.presentation.viewmodels.factories.ExerciseChoiceWorkoutSetFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ExerciseChoiceFragment : Fragment(), OnQueryTextListener {

    private var _binding: FragmentExerciseChoiceBinding? = null
    private val binding get() = _binding!!
    private val args: ExerciseChoiceFragmentArgs by navArgs()
    private val viewModel: ExerciseChoiceViewModel by viewModels {
        when (val setBuilder = args.setBuilder) {
            is SetDetailsBuilder.OfPlanEdit -> ExerciseChoicePlanEditFactory(setBuilder)
            is SetDetailsBuilder.OfWorkoutSet -> ExerciseChoiceWorkoutSetFactory(setBuilder)
        }
    }

    private lateinit var exercisesAdapter: ExerciseSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExerciseChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                initAdapter()
                addRecyclerViewDecorator()
                setPreviousChosenExercise()
                executeSearchViewCurrentQuery()
                handleScroll()
                binding.searchView.setOnQueryTextListener(this@ExerciseChoiceFragment)
            }
        }
    }

    private fun setPreviousChosenExercise() {
        lifecycleScope.launch {
            viewModel.selectedExercise?.id?.let { exercisesAdapter.setChosenExerciseId(it) }
                ?: viewModel.chosenExerciseIdOnLaunch.collectLatest { id ->
                    id?.let { exercisesAdapter.setChosenExerciseId(it) }
                }
        }
    }

    private fun handleScroll() {
        binding.exercisesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (binding.searchView.hasFocus()) {
                    binding.searchView.clearFocus()
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query == null) {
            return true
        }
        val searchQuery = "%$query%"
        lifecycleScope.launch {
            viewModel.searchForIconExercise(searchQuery).also { foundExercises ->
                exercisesAdapter.submitList(foundExercises)
            }
        }
        return true
    }

    private fun initAdapter() {
        exercisesAdapter = ExerciseSelectionAdapter(
            viewModel::onExerciseClicked,
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        ).also { binding.exercisesRecyclerView.adapter = it }
    }

    private fun executeSearchViewCurrentQuery() {
        val savedSearchViewQuery = binding.searchView.query.toString()

        if (savedSearchViewQuery.isEmpty()) {
            lifecycleScope.launch {
                val exercises = viewModel.getExercisesWithTopChosen()
                exercisesAdapter.submitList(exercises)
            }
        } else {
            onQueryTextChange(savedSearchViewQuery)
        }
    }

    private fun addRecyclerViewDecorator() {
        binding.exercisesRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
            ).also {
                val dividerDrawable = ContextCompat.getDrawable(
                    requireContext(), R.drawable.divider
                )!!
                it.setDrawable(dividerDrawable)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}