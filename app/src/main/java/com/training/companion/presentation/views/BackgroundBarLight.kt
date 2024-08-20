package com.training.companion.presentation.views

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.training.companion.R
import kotlin.math.min

class BackgroundBarLight(context: Context) : View(context) {

    private val color = context.getColor(R.color.orange)
    private val barThickness = context.resources.getDimension(R.dimen.time_progress_bar_bg_light_width)
    private val alpha = (0.25f * 255).toInt()
    private val blurRadius = context.resources.getDimension(R.dimen.time_progress_bar_bg_light_blur_radius)
    val thickness = barThickness + blurRadius

    private val paint = Paint().also {
        it.color = color
        it.style = Paint.Style.STROKE
        it.strokeWidth = barThickness
        it.alpha = alpha
        it.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            (min(width, height).toFloat() - thickness) / 2,
            paint
        )
    }
}