package com.training.companion.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.training.companion.R

class CountdownProgressBar(context: Context) : View(context) {

    private lateinit var drawRect: RectF

    private val backgroundBarColor = context.getColor(R.color.white)
    private val foregroundBarColor = context.getColor(R.color.orange)
    val thickness = context.resources.getDimension(R.dimen.time_progress_bar_width)

    private val backgroundBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = backgroundBarColor
        it.style = Paint.Style.STROKE
        it.strokeWidth = thickness
    }

    private val foregroundBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = foregroundBarColor
        it.style = Paint.Style.STROKE
        it.strokeWidth = thickness
    }

    private val startAngle = -90f

    var initialTimeMs = 0f
        set(value) {
            field = value
            invalidate()
        }
    var remainingTimeMs = initialTimeMs
        set(value) {
            field = value
            invalidate()
        }

    fun updateRemainingTimeOn(diffMs: Float) {
        remainingTimeMs -= diffMs
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawRect = RectF(
            thickness / 2 + paddingLeft,
            thickness / 2 + paddingTop,
            width - thickness / 2 + paddingRight,
            height - thickness / 2 + paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        val currentAngle = if (initialTimeMs != 0f) {
            remainingTimeMs / initialTimeMs * 360f
        } else {
            0f
        }
        drawBackgroundBar(canvas, currentAngle)
        if (initialTimeMs != 0f) {
            drawForegroundBar(canvas, currentAngle)
        }
    }

    private fun drawBackgroundBar(canvas: Canvas, angle: Float) {
        canvas.drawArc(
            drawRect,
            angle + startAngle,
            360f - angle,
            false,
            backgroundBarPaint
        )
    }

    private fun drawForegroundBar(canvas: Canvas, angle: Float) {
        canvas.drawArc(drawRect, startAngle, angle, false, foregroundBarPaint)
    }
}