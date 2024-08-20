package com.training.companion.presentation.util.behavior

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class BottomViewVisibilityBehavior(
    context: Context, attrs: AttributeSet,
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private var isHidden = false
    private var isAnimating = false

    private val slideDownListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            isAnimating = false
            isHidden = true
        }
    }

    private val slideUpListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            isAnimating = false
            isHidden = false
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int,
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        return dependency is RecyclerView
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {
        if (dyConsumed > 0) {
            hideView(child)
        } else if (dyConsumed < 0) {
            showView(child)
        }
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

    private fun hideView(view: View) {
        if (!isHidden && !isAnimating) {
            animateSlideDown(view)
        }
    }

    private fun showView(view: View) {
        if (isHidden && !isAnimating) {
            animateSlideUp(view)
        }
    }

    private fun animateSlideUp(view: View) {
        animateSlideInternal(view, -view.height, slideUpListener)
    }

    private fun animateSlideDown(view: View) {
        animateSlideInternal(view, view.height, slideDownListener)
    }

    private fun animateSlideInternal(view: View, dy: Int, listener: AnimatorListenerAdapter) {
        isAnimating = true
        val currentCoordinateY = view.y
        view.animate()
            .y(currentCoordinateY + dy)
            .setDuration(SLIDE_DURATION)
            .setListener(listener)
            .start()
    }

    private companion object {
        const val SLIDE_DURATION = 300L
    }
}