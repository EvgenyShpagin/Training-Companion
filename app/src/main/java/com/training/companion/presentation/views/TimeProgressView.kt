package com.training.companion.presentation.views

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.training.companion.R
import com.training.companion.domain.models.Time

class TimeProgressView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    private val timeProgressBar = CountdownProgressBar(context)
    private val barLight = BackgroundBarLight(context)
    private val timeTextView = TextView(context).also {
        it.text = context.getString(R.string.init_time_00_00)
    }

    private val handler = Handler(Looper.myLooper()!!)
    private val smoothProgressBarUpdateRunnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, REFRESH_DELAY)
            timeProgressBar.updateRemainingTimeOn(REFRESH_DELAY_F)
        }
    }

    private var smoothProgressBarUpdateIsActive = false

    init {
        updateTextAppearance()
        updateLayoutParams()
        addView(timeProgressBar)
        addView(timeTextView)
        addView(barLight)
    }

    fun setTime(time: Time) {
        timeTextView.text = time.toString(Time.Format.MM_SS)

        if (smoothProgressBarUpdateIsActive) {
            handler.removeCallbacks(smoothProgressBarUpdateRunnable)
            smoothProgressBarUpdateIsActive = false
        }

        if (timeProgressBar.initialTimeMs != 0f) {
            timeProgressBar.remainingTimeMs = time.totalSeconds * 1000f
            if (time.totalSeconds != 0) {
                smoothProgressBarUpdateIsActive = true
                handler.post(smoothProgressBarUpdateRunnable)
            }
        }
    }

    fun setInitTime(time: Time) {
        val timeMillis = time.totalSeconds * 1000f
        timeProgressBar.initialTimeMs = timeMillis
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        timeTextView.measure(
            MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.UNSPECIFIED)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        barLight.layout(0, 0, right - left, bottom - top)
        val progressBarPadding =
            (barLight.thickness.toInt() - timeProgressBar.thickness.toInt()) / 2
        timeProgressBar.layout(
            progressBarPadding,
            progressBarPadding,
            right - left - progressBarPadding,
            bottom - top - progressBarPadding
        )
        val childWidth = timeTextView.measuredWidth
        val childHeight = timeTextView.measuredHeight

        val childLeft = (measuredWidth - childWidth) / 2
        val childTop = (measuredHeight - childHeight) / 2

        timeTextView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
    }

    private fun updateLayoutParams() {
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).also {
            barLight.layoutParams = it
            timeProgressBar.layoutParams = it
        }
    }

    private fun updateTextAppearance() = timeTextView.apply {
        textSize = context.resources.getDimension(R.dimen.time_progress_text_size)
        includeFontPadding = false
        val gradientShader = LinearGradient(
            0f,
            0f,
            0f,
            textSize * 1.5f,
            intArrayOf(context.getColor(R.color.white), context.getColor(R.color.orange)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = gradientShader
    }

    companion object {
        private const val REFRESH_RATE = 50
        private const val REFRESH_DELAY = 1000L / REFRESH_RATE
        private const val REFRESH_DELAY_F = REFRESH_DELAY.toFloat()
    }
}