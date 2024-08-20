package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.training.companion.R
import com.training.companion.databinding.FragmentEquipmentFilterBinding
import com.training.companion.presentation.recyclerview.adapters.EquipmentFilterAdapter
import com.training.companion.presentation.recyclerview.decorations.MarginItemDecoration
import com.training.companion.presentation.viewmodels.EquipmentFilterViewModel
import com.training.companion.presentation.viewmodels.factories.EquipmentFilterFactory
import kotlinx.coroutines.launch

class EquipmentFilterFragment : Fragment() {

    private var _binding: FragmentEquipmentFilterBinding? = null
    private val binding get() = _binding!!

    private val args: EquipmentFilterFragmentArgs by navArgs()

    private val viewModel: EquipmentFilterViewModel by viewModels {
        EquipmentFilterFactory(args.setBuilder)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEquipmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                lifecycleScope.launch {
                    val equipmentList = viewModel.getEquipmentList()

                    val verticalMargin =
                        resources.getDimension(R.dimen.exercise_filter_items_margin)
                    val horizontalMargin = resources.getDimension(R.dimen.large_margin)

                    binding.equipmentFilterList.apply {
                        adapter = EquipmentFilterAdapter(
                            context = requireContext(),
                            equipmentList = equipmentList,
                            onEquipmentClick = viewModel::onEquipmentClick,
                            whichAlreadyChecked = viewModel.getWhichAreChosen()
                        )
                        if (itemDecorationCount > 0) {
                            removeItemDecorationAt(0)
                        }
                        addItemDecoration(
                            MarginItemDecoration(
                                verticalSpace = verticalMargin,
                                horizontalSpace = horizontalMargin,
                                withTopItemMargin = true
                            )
                        )
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}