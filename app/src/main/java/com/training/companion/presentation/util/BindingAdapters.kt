package com.training.companion.presentation.util

import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.IntRange
import androidx.annotation.IntegerRes
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.transition.TransitionSet.ORDERING_SEQUENTIAL
import com.google.android.material.button.MaterialButtonToggleGroup

@BindingAdapter("isSelected")
fun isSelected(view: View, value: Boolean) {
    view.isSelected = value
}

@BindingAdapter("exerciseImage")
fun setExerciseImage(imageView: ImageView, imageName: String?) {
    if (imageName == null) return
    val assets = imageView.context.assets
    val icon = assets.getExerciseIcon(imageName)
    imageView.setImageDrawable(icon)
}

@BindingAdapter("onSegmentedToggleGroupClick", requireAll = false)
fun addOnButtonCheckedListener(
    group: MaterialButtonToggleGroup, listener: MaterialButtonToggleGroup.OnButtonCheckedListener,
) {
    group.addOnButtonCheckedListener(listener)
}

@BindingAdapter("segmentedToggleGroupChecked")
fun defaultSegmentedToggleGroupChecked(group: MaterialButtonToggleGroup, id: Int) {
    group.check(id)
}

@BindingAdapter("animateOnAppear")
fun animateOnAppear(view: View, visibility: Int) {
    if (view.visibility != View.VISIBLE && visibility == View.VISIBLE) {
        TransitionManager.beginDelayedTransition(view.rootView as ViewGroup)
    }
    view.visibility = visibility
}

@BindingAdapter("visibility", "appearDuration", requireAll = true)
fun animateViewOnAppear(
    view: View, visibility: Int,
    @IntegerRes durationResource: Int,
) {
    if (view.visibility != visibility) {
        if (visibility == View.VISIBLE) {
            val endAlpha = view.alpha
            val duration = view.resources.getInteger(durationResource).toLong()
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(endAlpha)
                .setDuration(duration)
                .setListener(null)
                .start()
        } else {
            if (view.animation != null) {
                view.animation.cancel()
            }
        }
        view.visibility = visibility
    }
}

@BindingAdapter("onBackPressed")
fun doOnBackPressed(fragmentView: View, onClickListener: OnClickListener) {
    val fragment: Fragment = fragmentView.findFragment()
    fragment.requireActivity().onBackPressedDispatcher.addCallback(owner = fragment) {
        onClickListener.onClick(fragmentView)
    }
}

@BindingAdapter("transitionAnimatedVisibility")
fun setVisibilityAnimationByTransition(view: View, visibility: Int) {
    if (view.visibility != visibility) {
        val transition = TransitionSet()
        transition.ordering = ORDERING_SEQUENTIAL
        if (visibility == View.VISIBLE) {
            transition.addTransition(ChangeBounds()).addTransition(Fade())
        } else {
            transition.addTransition(Fade()).addTransition(ChangeBounds())
        }

        TransitionManager.beginDelayedTransition(view.parent as ViewGroup, transition)
        view.visibility = visibility
    }
}

@BindingAdapter("layoutMarginBottom")
fun setLayoutMarginBottom(view: View, value: Float) {
    view.updateLayoutParams<MarginLayoutParams> { bottomMargin = value.toInt() }
}

@BindingAdapter("textStyle")
fun setTextStyle(textView: TextView, @IntRange(0, 3) value: Int) {
    textView.setTypeface(textView.typeface, value)
}