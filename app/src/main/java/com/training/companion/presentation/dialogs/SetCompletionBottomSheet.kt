package com.training.companion.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.training.companion.databinding.BottomSheetSetCompletionBinding
import com.training.companion.presentation.viewmodels.SetCompletionViewModel
import com.training.companion.presentation.viewmodels.factories.SetCompletionFactory


class SetCompletionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSetCompletionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SetCompletionViewModel by viewModels { SetCompletionFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetSetCompletionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        isCancelable = false

        requireDialog().setOnShowListener {
            setRequiredSheetHeight(it)
        }
    }

    private fun setRequiredSheetHeight(dialogInterface: DialogInterface) {
        val dialog = dialogInterface as BottomSheetDialog
        binding.bottomSheet.let { sheet ->
            dialog.behavior.peekHeight = sheet.height
            sheet.parent.parent.requestLayout()
        }
    }
}