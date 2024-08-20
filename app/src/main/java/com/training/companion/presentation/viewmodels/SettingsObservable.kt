package com.training.companion.presentation.viewmodels

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.training.companion.BR
import com.training.companion.R
import com.training.companion.data.repositories.AppSettings
import com.training.companion.data.repositories.AppSettings.APP_EXTRA_CONFIRM_ACTION
import com.training.companion.data.repositories.AppSettings.APP_LANGUAGE
import com.training.companion.data.repositories.AppSettings.APP_LENGTH_UNITS
import com.training.companion.data.repositories.AppSettings.APP_REMIND_WORKOUT_TIME
import com.training.companion.data.repositories.AppSettings.APP_SOUND_ENABLED
import com.training.companion.data.repositories.AppSettings.APP_VIBRATION_ENABLED
import com.training.companion.data.repositories.AppSettings.APP_WEIGHT_UNITS
import com.training.companion.domain.enums.ExtraConfirmAction
import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.LengthUnit
import com.training.companion.domain.enums.WeightUnit

class SettingsObservable : BaseObservable() {

    private val onSettingsChangedListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            APP_REMIND_WORKOUT_TIME -> notifyPropertyChanged(BR.workoutReminderText)
            APP_EXTRA_CONFIRM_ACTION -> notifyPropertyChanged(BR.extraConfirmActionTextRes)
            APP_LANGUAGE -> notifyPropertyChanged(BR.languageTextRes)
            APP_LENGTH_UNITS -> notifyPropertyChanged(BR.unitsTextRes)
            APP_WEIGHT_UNITS -> notifyPropertyChanged(BR.unitsTextRes)
            APP_SOUND_ENABLED -> notifyPropertyChanged(BR.soundEnabled)
            APP_VIBRATION_ENABLED -> notifyPropertyChanged(BR.vibrationEnabled)
        }
    }

    init {
        AppSettings.registerOnChangeListener(onSettingsChangedListener)
    }

    @get:Bindable
    val workoutReminderText get() = AppSettings.remindWorkoutTime.toString()

    @get:Bindable
    val extraConfirmActionTextRes: Int
        get() {
            return when (AppSettings.extraConfirmAction) {
                ExtraConfirmAction.None -> R.string.setting_extra_confirm_not_set
                ExtraConfirmAction.DoubleVolumeDown -> R.string.setting_extra_confirm_doubleDown
                ExtraConfirmAction.DoubleVolumeUp -> R.string.setting_extra_confirm_doubleUp
            }
        }

    @get:Bindable
    val languageTextRes: Int
        get() {
            return when (AppSettings.language) {
                Language.English -> R.string.setting_language_english
                Language.Russian -> R.string.setting_language_russian
                Language.Spanish -> R.string.setting_language_spanish
            }
        }

    @get:Bindable
    val unitsTextRes: Int
        get() {
            val units = AppSettings.units
            return if (units.weightUnit == WeightUnit.Kilograms
                && units.lengthUnit == LengthUnit.MetersCentimeters
            ) {
                R.string.setting_units_kg_cm_m
            } else if (units.weightUnit == WeightUnit.Kilograms
                && units.lengthUnit == LengthUnit.FeetInches
            ) {
                R.string.setting_units_kg_ft_inch
            } else if (units.weightUnit == WeightUnit.Pounds
                && units.lengthUnit == LengthUnit.MetersCentimeters
            ) {
                R.string.setting_units_pd_cm_m
            } else {
                R.string.setting_units_pd_ft_inch
            }
        }

    @get:Bindable
    val soundEnabled get() = AppSettings.soundIsOn

    @get:Bindable
    val vibrationEnabled get() = AppSettings.vibrationIsOn
}