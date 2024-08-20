package com.training.companion.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.training.companion.domain.enums.ExtraConfirmAction
import com.training.companion.domain.enums.Language
import com.training.companion.domain.enums.LengthUnit
import com.training.companion.domain.enums.WeightUnit
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.Units
import com.training.companion.domain.util.getSystemLanguage
import com.training.companion.presentation.fragments.toplevel.SettingsFragment

private const val DEFAULT_REMIND_TIME = "16:00"
private val DEFAULT_EXTRA_CONFIRM_ACTION = ExtraConfirmAction.None
private val DEFAULT_LANGUAGE = getSystemLanguage() ?: Language.English
private val DEFAULT_UNITS = Units(LengthUnit.MetersCentimeters, WeightUnit.Kilograms)
private const val DEFAULT_SOUND_MODE = true
private const val DEFAULT_VIBRO_MODE = true


object AppSettings {

    private var mSharedPrefs: SharedPreferences? = null

    private val sharedPrefs
        get() = mSharedPrefs ?: throw UninitializedPropertyAccessException(
            "AppSettings : sharedPreferences : Not Yet Initialized"
        )

    fun initSharedPrefs(context: Context) {
        mSharedPrefs = context.getSharedPreferences(
            APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    val remindWorkoutTime: Time
        get() {
            return Time.fromString(
                sharedPrefs.getString(
                    APP_REMIND_WORKOUT_TIME,
                    DEFAULT_REMIND_TIME
                )!!
            )
        }

    val extraConfirmAction: ExtraConfirmAction
        get() {
            return ExtraConfirmAction.entries[
                    sharedPrefs.getInt(
                        APP_EXTRA_CONFIRM_ACTION,
                        DEFAULT_EXTRA_CONFIRM_ACTION.ordinal
                    )
            ]
        }

    val language: Language
        get() {
            return Language.entries[sharedPrefs.getInt(
                APP_LANGUAGE,
                DEFAULT_LANGUAGE.ordinal
            )]
        }

    val units: Units
        get() {
            return Units(
                LengthUnit.entries[sharedPrefs.getInt(
                    APP_LENGTH_UNITS,
                    DEFAULT_UNITS.lengthUnit.ordinal
                )],
                WeightUnit.entries[sharedPrefs.getInt(
                    APP_WEIGHT_UNITS,
                    DEFAULT_UNITS.weightUnit.ordinal
                )]
            )
        }

    val soundIsOn: Boolean
        get() = sharedPrefs.getBoolean(APP_SOUND_ENABLED, DEFAULT_SOUND_MODE)

    val vibrationIsOn: Boolean
        get() = sharedPrefs.getBoolean(APP_VIBRATION_ENABLED, DEFAULT_VIBRO_MODE)

    fun SettingsFragment.editRemindWorkoutTime(time: Time) {
        sharedPrefs
            .edit()
            .putString(APP_REMIND_WORKOUT_TIME, time.toString())
            .apply()
    }

    fun SettingsFragment.editExtraConfirmAction(action: ExtraConfirmAction) {
        sharedPrefs
            .edit()
            .putInt(APP_EXTRA_CONFIRM_ACTION, action.ordinal)
            .apply()
    }

    fun SettingsFragment.editLanguage(language: Language) {
        sharedPrefs
            .edit()
            .putInt(APP_EXTRA_CONFIRM_ACTION, language.ordinal)
            .apply()
    }

    fun SettingsFragment.editUnits(units: Units) {
        sharedPrefs
            .edit()
            .putInt(APP_LENGTH_UNITS, units.lengthUnit.ordinal)
            .putInt(APP_WEIGHT_UNITS, units.weightUnit.ordinal)
            .apply()
    }

    fun SettingsFragment.editSoundMode(isEnabled: Boolean) {
        sharedPrefs
            .edit()
            .putBoolean(APP_SOUND_ENABLED, isEnabled)
            .apply()
    }

    fun SettingsFragment.editVibrationMode(isEnabled: Boolean) {
        sharedPrefs
            .edit()
            .putBoolean(APP_VIBRATION_ENABLED, isEnabled)
            .apply()
    }

    const val APP_PREFERENCES = "app-settings"
    const val APP_REMIND_WORKOUT_TIME = "app-remind-time"
    const val APP_EXTRA_CONFIRM_ACTION = "app-extra-confirm"
    const val APP_LANGUAGE = "app-language"
    const val APP_LENGTH_UNITS = "app-l-units"
    const val APP_WEIGHT_UNITS = "app-w-units"
    const val APP_SOUND_ENABLED = "app-sound-enabled"
    const val APP_VIBRATION_ENABLED = "app-vibration-enabled"
}