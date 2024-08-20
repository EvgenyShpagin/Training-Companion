package com.training.companion.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.training.companion.R
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.models.Reps
import com.training.companion.domain.models.Time
import com.training.companion.presentation.util.getDP

class CompletedSetsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {

    private var title: String = ""

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CompletedExercisesView,
            0, 0
        ).apply {
            try {
                title = getString(R.styleable.CompletedExercisesView_exercises_title) ?: ""
            } finally {
                recycle()
            }
        }
    }

    private val colors = object {
        val ofBackground = context.getColor(R.color.chinese_black)
        val ofText = context.getColor(R.color.workout_container_text_color)
        val ofDivider = context.getColor(R.color.dark_charcoal)
    }

    private val completedSets = Array<CompletedSet?>(2) { null }
    private val exerciseNames = Array(2) { "" }

    private val dimens = object {
        val surfaceCornerRadius =
            context.resources.getDimension(R.dimen.completed_exercises_surface_corner_radius)
        val surfaceRectF = RectF(0f, 0f, 0f, 0f)
        val textSize = context.resources.getDimension(R.dimen.completed_exercises_text_size)
        val horizontalPadding = getDP(resources.displayMetrics, HORIZONTAL_PADDING)
        val paddingAfterExerciseName = horizontalPadding * 2
        val requiredTitleHeight = getDP(resources.displayMetrics, MIN_TITLE_HEIGHT)
        val requiredExerciseHeight = getDP(resources.displayMetrics, MIN_EXERCISE_HEIGHT)

        var titleTextHeight = 0f
        var exerciseHeight = 0f
        var exerciseNameWidth = 0f
        var requiredExerciseNameWidth = 0f
        var ordinalWidth = 0f
        var repsWidth = 0f
        var durationWidth = 0f
        var requiredWidth = 0
        var requiredHeight = 0
        var extraSpace = 0f
    }

    private val paint = object {
        val ofTitle = Paint().also {
            it.color = colors.ofText
            it.textAlign = Paint.Align.CENTER
            it.textSize = dimens.textSize
            it.alpha = (255 * 0.8f).toInt()
        }
        val ofSet = Paint().also {
            it.color = colors.ofText
            it.textSize = dimens.textSize
        }
        val ofSurface = Paint().also {
            it.color = colors.ofBackground
            it.flags = Paint.ANTI_ALIAS_FLAG
        }
        val ofDivider = Paint().also {
            it.color = colors.ofDivider
            it.style = Paint.Style.STROKE
            it.strokeWidth = 2f
        }
    }

    private val secondsSuffix = context.getString(R.string.seconds_short)

    private fun setExercise(position: Int, completedExercise: CompletedSet) {
        completedSets[position] = completedExercise.also {
            exerciseNames[position] = it.exercise.name
        }
        requestLayout()
    }

    fun setExercises(list: List<CompletedSet>) {
        list.forEachIndexed { index, completedSet ->
            setExercise(index, completedSet)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requiredWidth = getRequiredViewWidth().toInt()
        val requiredHeight = getRequiredViewHeight().toInt()
        setMeasuredDimension(
            resolveSize(requiredWidth, widthMeasureSpec),
            resolveSize(requiredHeight, heightMeasureSpec)
        )
        dimens.requiredWidth = requiredWidth
        dimens.requiredHeight = requiredHeight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val exerciseCount = completedSets.count { it != null }

        if (h != dimens.requiredHeight) {
            onHeightChanged(newHeight = h, exerciseCount = exerciseCount)
        } else {
            dimens.titleTextHeight = dimens.requiredTitleHeight
            dimens.exerciseHeight = dimens.requiredExerciseHeight
        }

        if (w != dimens.requiredWidth) {
            onWidthChanged(newWidth = w)
        } else {
            dimens.exerciseNameWidth = dimens.requiredExerciseNameWidth
        }
        dimens.surfaceRectF.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    private fun onWidthChanged(newWidth: Int) {
        val widthDiff = dimens.requiredWidth - newWidth

        completedSets.forEachIndexed { index, set ->
            if (set == null) return
            val name = set.exercise.name
            val nameWidth = paint.ofSet.measureText(name)
            val maxNameWidth = dimens.requiredExerciseNameWidth - widthDiff
            if (nameWidth > maxNameWidth) {
                var substring = name.dropLast(2)
                var substringWidth = paint.ofSet.measureText(substring + ELLIPSIZE)
                while (substringWidth > maxNameWidth && substring.length >= 2) {
                    substring = substring.dropLast(2)
                    substringWidth = paint.ofSet.measureText(substring + ELLIPSIZE)
                }
                exerciseNames[index] = substring + ELLIPSIZE
                dimens.exerciseNameWidth = substringWidth
                Log.d(TAG, "onWidthChanged: new exercise name = ${exerciseNames[index]}")
            }
        }
    }

    private fun onHeightChanged(newHeight: Int, exerciseCount: Int) {
        when (exerciseCount) {
            1 -> {
                dimens.titleTextHeight = 0.4f
                dimens.exerciseHeight = 0.6f
            }

            2 -> {
                dimens.titleTextHeight = 0.3f * newHeight
                dimens.exerciseHeight = 0.35f * newHeight
            }

            else -> {
                dimens.titleTextHeight = newHeight.toFloat()
                Log.d(TAG, "No one exercise was added")
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawSurface(canvas)
        drawTitle(canvas)
        drawDivider(canvas)
        completedSets.apply {
            get(0)?.let { drawSet(canvas, it, exerciseNames[0]) }
            get(1)?.let {
                drawDivider(canvas)
                drawSet(canvas, it, exerciseNames[1])
            }
        }
    }

    private fun drawSurface(canvas: Canvas) {
        canvas.drawRoundRect(
            dimens.surfaceRectF,
            dimens.surfaceCornerRadius,
            dimens.surfaceCornerRadius,
            paint.ofSurface
        )
    }

    private fun drawTitle(canvas: Canvas) {
        canvas.drawText(
            title,
            dimens.surfaceRectF.right / 2f,
            dimens.titleTextHeight / 2 - (paint.ofTitle.descent() + paint.ofTitle.ascent()) / 2,
            paint.ofTitle
        )
        canvas.translate(0f, dimens.titleTextHeight)
    }

    private fun drawDivider(canvas: Canvas) {
        canvas.drawLine(
            0f,
            0f,
            dimens.surfaceRectF.right,
            0f,
            paint.ofDivider
        )
    }

    private fun drawSet(canvas: Canvas, set: CompletedSet, fixedExerciseName: String) {
        val centerY =
            dimens.exerciseHeight / 2 - (paint.ofSet.descent() + paint.ofSet.ascent()) / 2

        canvas.save()

        paint.ofSet.textAlign = Paint.Align.LEFT

        canvas.drawText(
            getOrdinalText(set.ordinal),
            dimens.horizontalPadding,
            centerY,
            paint.ofSet
        )
        canvas.translate(dimens.horizontalPadding + dimens.ordinalWidth, 0f)
        canvas.drawText(
            fixedExerciseName,
            dimens.horizontalPadding,
            centerY,
            paint.ofSet
        )
        canvas.translate(dimens.horizontalPadding + dimens.exerciseNameWidth, 0f)
        set.reps?.let {
            canvas.drawText(
                getRepsText(it),
                dimens.paddingAfterExerciseName + dimens.extraSpace,
                centerY,
                paint.ofSet
            )
        }
        canvas.restore()
        paint.ofSet.textAlign = Paint.Align.RIGHT

        canvas.drawText(
            getDurationText(set.duration),
            dimens.surfaceRectF.right - dimens.horizontalPadding,
            centerY,
            paint.ofSet
        )
        canvas.translate(0f, dimens.exerciseHeight)
    }

    private fun getRequiredViewHeight(): Float {
        val setsCount = completedSets.count { it != null }
        return dimens.requiredTitleHeight + dimens.requiredExerciseHeight * setsCount
    }

    private fun getRequiredViewWidth(): Float {
        val maxSetWidth = getMaxSetWidth()
        val titleWidth = paint.ofTitle.measureText(title)
        return if (titleWidth > maxSetWidth) {
            dimens.extraSpace = titleWidth - maxSetWidth
            titleWidth + dimens.horizontalPadding * 2f
        } else {
            dimens.extraSpace = 0f
            maxSetWidth + dimens.horizontalPadding * 2f
        }
    }

    private fun getMaxSetWidth(): Float {
        fun maxWidth(selector: (set: CompletedSet) -> String) =
            completedSets.maxOf { it?.let { paint.ofSet.measureText(selector(it)) } ?: 0f }

        val ordinalMaxWidth = maxWidth { getOrdinalText(it.ordinal) }
            .also { dimens.ordinalWidth = it }
        val exerciseNameMaxWidth = maxWidth { it.exercise.name }
            .also { dimens.requiredExerciseNameWidth = it }
        val repsMaxWidth = maxWidth { getRepsText(it.reps) }
            .also { dimens.repsWidth = it }
        val durationMaxWidth = maxWidth { getDurationText(it.duration) }
            .also { dimens.durationWidth = it }
        val paddings = if (repsMaxWidth != 0f) {
            dimens.horizontalPadding * 2 + dimens.paddingAfterExerciseName
        } else {
            dimens.horizontalPadding + dimens.paddingAfterExerciseName
        }
        return ordinalMaxWidth + exerciseNameMaxWidth + repsMaxWidth +
                durationMaxWidth + paddings
    }

    private fun getDurationText(duration: Time): String {
        return "${duration.totalSeconds}$secondsSuffix"
    }

    private fun getRepsText(reps: Reps.Exact?): String {
        return reps?.let {
            context.resources.getQuantityString(
                R.plurals.repetitions_with_value,
                reps.value,
                reps.value
            )
        } ?: ""
    }

    private fun getOrdinalText(ordinal: Int): String {
        return "${ordinal + 1}."
    }


    companion object {
        private const val MIN_TITLE_HEIGHT = 28f
        private const val MIN_EXERCISE_HEIGHT = 32f
        private const val HORIZONTAL_PADDING = 12f
        private const val ELLIPSIZE = "..."

        const val TAG = "CompletedExercisesView"
    }
}
