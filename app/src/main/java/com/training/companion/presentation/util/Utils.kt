package com.training.companion.presentation.util

import android.content.ContextWrapper
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.training.companion.R
import com.training.companion.domain.enums.Action
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Time
import com.training.companion.domain.util.getExerciseImageDirectoryPath
import com.training.companion.presentation.WorkoutService
import java.text.DecimalFormat
import java.util.Locale

fun getDP(displayMetrics: DisplayMetrics, value: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics
    )
}

fun getWorkoutTypeFromId(@IntRange(from = 1, to = 4) id: Int): WorkoutType {
    return WorkoutType.entries.find { it.id == id }!!
}

fun AssetManager.getExerciseIcon(imageName: String): Drawable {
    val filename = getExerciseImageDirectoryPath() + imageName
    return Drawable.createFromStream(open(filename), null)!!
}

fun startWorkoutService(view: View) {
    val activity = getActivityFromView(view)!!
    Intent(activity, WorkoutService::class.java).also {
        it.action = WorkoutService.START
        activity.startService(it)
    }
}

fun stopWorkoutService(view: View) {
    val activity = getActivityFromView(view)!!
    Intent(activity, WorkoutService::class.java).also {
        it.action = WorkoutService.STOP
        activity.stopService(it)
    }
}

private fun getActivityFromView(view: View): AppCompatActivity? {
    var context = view.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun MenuItem.setEnabledWithTint(isEnabled: Boolean, disabledStateAlpha: Int = 255 / 2) {
    this.isEnabled = isEnabled
    icon?.mutate()?.alpha = if (isEnabled) {
        255
    } else {
        disabledStateAlpha
    }
}

val WorkoutType.nameStringRes
    get() = when (this) {
        WorkoutType.Strength -> R.string.strength
        WorkoutType.Cardio -> R.string.cardio
        WorkoutType.Functional -> R.string.functional
        WorkoutType.YogaOrPilates -> R.string.yoga_and_pilates
    }

//todo
fun NavController.navigateBack(@IdRes destId: Int) {
    if (!popBackStack(destId, false)) {
        navigate(destId)
    }
}

fun View.hideKeyboard(imm: InputMethodManager): Boolean {
    return try {
        imm.hideSoftInputFromWindow(windowToken, 0)
        true
    } catch (e: Exception) {
        false
    }
}

val numberPickerTimeFormatter =
    android.widget.NumberPicker.Formatter { value ->
        String.format(
            Locale.getDefault(),
            "%02d",
            value
        )
    }

val decimalFormat0_0 = DecimalFormat("0.#")

fun totalTimeToString(time: Time): String {
    return if (time.hours > 0) {
        String.format(
            Locale.getDefault(),
            Time.Format.H_MM_SS.pattern,
            time.hours,
            time.minutes,
            time.seconds
        )
    } else {
        String.format(Locale.getDefault(), Time.Format.MM_SS.pattern, time.minutes, time.seconds)
    }
}

val Action.nameResource
    get() = when (this) {
        Action.GO_TO_REST -> R.string.action_go_to_rest
        Action.START_EXERCISE -> R.string.action_go_to_exercise
        Action.ADD_EXTRA_STOPWATCH -> R.string.action_add_extra_stopwatch
        Action.CHANGE_REST_TIME -> R.string.action_change_rest_time
        Action.RETURN_TO_PREVIOUS_STAGE -> R.string.action_return_to_prev_stage
        Action.RESTART_SET -> R.string.action_restart_set
    }

val Action.iconResource
    get() = when (this) {
        Action.GO_TO_REST -> R.drawable.ic_rest_24
        Action.START_EXERCISE -> R.drawable.ic_exercise_24
        Action.ADD_EXTRA_STOPWATCH -> R.drawable.ic_add_extra_stopwatch
        Action.CHANGE_REST_TIME -> R.drawable.ic_more_time_24
        Action.RETURN_TO_PREVIOUS_STAGE -> R.drawable.ic_back_24
        Action.RESTART_SET -> R.drawable.ic_restart_24
    }

val WorkoutType.thumbnailResource
    get() = when (this) {
        WorkoutType.Strength -> R.drawable.strength_thumbnail
        WorkoutType.Cardio -> R.drawable.cardio_thumbnail
        WorkoutType.Functional -> R.drawable.functional_thumbnail
        WorkoutType.YogaOrPilates -> R.drawable.yoga_pilates_thumbnail
    }

val WorkoutType.nameResource
    get() = when (this) {
        WorkoutType.Strength -> R.string.strength_training
        WorkoutType.Cardio -> R.string.cardio_training
        WorkoutType.Functional -> R.string.functional_training
        WorkoutType.YogaOrPilates -> R.string.yoga_and_pilates_training
    }

val Resources.primaryLocale: Locale get() = configuration.getLocales()[0]