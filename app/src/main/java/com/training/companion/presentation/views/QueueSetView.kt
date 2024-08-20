package com.training.companion.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import com.training.companion.R
import com.training.companion.databinding.ViewQueueSetBinding
import com.training.companion.domain.models.Set

class QueueSetView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val binding: ViewQueueSetBinding
    private var set: Set? = null
    private val weightToRepsMargin = resources.getDimension(R.dimen.queue_set_chips_margin).toInt()

    init {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.view_queue_set,
            this,
            true
        )
    }

    fun setWorkoutSet(set: Set?) {
        this.set = set
        if (set == null) return

        val wasRepsVisible = binding.set?.reps != null
        val shouldRepsBeVisible = set.reps != null

        binding.set = set

        if (wasRepsVisible == shouldRepsBeVisible) return

        val layout = binding.root as ConstraintLayout
        val newConstraintSet = ConstraintSet().also { it.clone(layout) }

        if (shouldRepsBeVisible) {
            newConstraintSet.connect(
                binding.weight.id, ConstraintSet.START,
                binding.reps.id, ConstraintSet.END,
                weightToRepsMargin
            )
            newConstraintSet.connect(
                binding.title.id, ConstraintSet.BOTTOM,
                binding.weight.id, ConstraintSet.TOP
            )
        } else {
            newConstraintSet.connect(
                binding.weight.id, ConstraintSet.START,
                binding.title.id, ConstraintSet.START
            )
            newConstraintSet.connect(
                binding.title.id, ConstraintSet.BOTTOM,
                binding.icon.id, ConstraintSet.BOTTOM
            )
        }
        layout.setConstraintSet(newConstraintSet)
    }

    fun setEndIconVisible(value: Boolean) {
        binding.showEndIcon = value
    }
}