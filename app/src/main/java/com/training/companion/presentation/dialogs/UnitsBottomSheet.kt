package com.training.companion.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.training.companion.domain.models.Units
import com.training.companion.databinding.BottomSheetUnitsBinding
import com.training.companion.domain.enums.LengthUnit
import com.training.companion.domain.enums.WeightUnit
import com.training.companion.presentation.fragments.toplevel.SettingsFragment
import com.training.companion.data.repositories.AppSettings
import com.training.companion.data.repositories.AppSettings.editUnits


class UnitsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetUnitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetUnitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val units = AppSettings.units

        if (units.weightUnit == WeightUnit.Kilograms) {
            selectKilograms()
        } else {
            selectPounds()
        }

        if (units.lengthUnit == LengthUnit.MetersCentimeters) {
            selectMetersAndCm()
        } else {
            selectFeetAndInches()
        }
    }

    override fun onStart() {
        super.onStart()

        binding.kilosButton.setOnClickListener {
            selectKilograms()
        }
        binding.poundsButton.setOnClickListener {
            selectPounds()
        }
        binding.metersCmButton.setOnClickListener {
            selectMetersAndCm()
        }
        binding.feetInchesButton.setOnClickListener {
            selectFeetAndInches()
        }
    }

    private fun selectKilograms() {
        if (binding.kilosButton.isSelected) return
        binding.kilosButton.isSelected = true
        binding.poundsButton.isSelected = false
        (parentFragment as SettingsFragment).editUnits(
            Units(getCurrentLengthUnit(), WeightUnit.Kilograms)
        )
    }

    private fun selectPounds() {
        if (binding.poundsButton.isSelected) return
        binding.poundsButton.isSelected = true
        binding.kilosButton.isSelected = false
        (parentFragment as SettingsFragment).editUnits(
            Units(getCurrentLengthUnit(), WeightUnit.Pounds)
        )
    }

    private fun selectMetersAndCm() {
        if (binding.metersCmButton.isSelected) return
        binding.metersCmButton.isSelected = true
        binding.feetInchesButton.isSelected = false
        (parentFragment as SettingsFragment).editUnits(
            Units(LengthUnit.MetersCentimeters, getCurrentWeightUnit())
        )
    }

    private fun selectFeetAndInches() {
        if (binding.feetInchesButton.isSelected) return
        binding.feetInchesButton.isSelected = true
        binding.metersCmButton.isSelected = false
        (parentFragment as SettingsFragment).editUnits(
            Units(LengthUnit.FeetInches, getCurrentWeightUnit())
        )
    }

    private fun getCurrentLengthUnit(): LengthUnit {
        return if (binding.metersCmButton.isSelected)
            LengthUnit.MetersCentimeters
        else
            LengthUnit.FeetInches
    }

    private fun getCurrentWeightUnit(): WeightUnit {
        return if (binding.kilosButton.isSelected)
            WeightUnit.Kilograms
        else
            WeightUnit.Pounds
    }
}