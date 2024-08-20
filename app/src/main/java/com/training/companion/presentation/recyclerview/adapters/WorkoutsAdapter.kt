package com.training.companion.presentation.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.training.companion.R
import com.training.companion.databinding.ListItemWorkoutBinding
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.PastWorkout
import com.training.companion.domain.util.getCurrentDate
import com.training.companion.domain.util.getYesterdayDate
import com.training.companion.presentation.util.primaryLocale
import com.training.companion.presentation.util.thumbnailResource

class WorkoutsAdapter(private val onClickListener: OnWorkoutClickListener? = null) :
    ListAdapter<PastWorkout, WorkoutsAdapter.WorkoutViewHolder>(WorkoutDiffUtilCallback()) {

    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ListItemWorkoutBinding.bind(view)

        init {
            binding.root.setOnClickListener {
                onClickListener?.onClick(workoutId = getItem(adapterPosition).id)
            }
        }

        fun bind(workout: PastWorkout) {
            setWorkoutThumbnail(workout)
            setWorkoutLabel(workout)
            setWorkoutDate(workout)
            setWorkoutPerformance(workout)
        }

        private fun setWorkoutThumbnail(workout: PastWorkout) {
            binding.trainingTypeIcon.setImageResource(workout.type.thumbnailResource)
        }

        private fun setWorkoutLabel(workout: PastWorkout) {
            binding.typeLabel.setText(
                when (workout.type) {
                    WorkoutType.Strength -> R.string.strength
                    WorkoutType.Cardio -> R.string.cardio
                    WorkoutType.Functional -> R.string.functional
                    WorkoutType.YogaOrPilates -> R.string.yoga_and_pilates
                }
            )
        }

        private fun setWorkoutDate(workout: PastWorkout) {
            val resources = binding.root.resources
            val datetime = workout.finishDatetime
            binding.dateText.text = if (datetime.dateIsTheSame(other = getCurrentDate())) {
                resources.getString(R.string.label_today)
            } else if (datetime.dateIsTheSame(other = getYesterdayDate())) {
                resources.getString(R.string.label_yesterday)
            } else {
                datetime.getFormattedDate(resources.primaryLocale)
            }
        }

        private fun setWorkoutPerformance(workout: PastWorkout) {
            fun getString(res: Int, quantity: Int) =
                binding.root.resources.getQuantityString(res, quantity)

            val exerciseCountText = getString(R.plurals.exercise_count, workout.exercises.count())
            val setsCountText = getString(R.plurals.sets_count, workout.sets.count())
            val bodyPartCountText = getString(
                R.plurals.body_part_count_short, workout.includedBodyPart.count()
            )
            val text = "$exerciseCountText • $setsCountText • $bodyPartCountText"
            binding.performanceText.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_plan, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun interface OnWorkoutClickListener {
        fun onClick(workoutId: Int)
    }
}

private class WorkoutDiffUtilCallback : DiffUtil.ItemCallback<PastWorkout>() {
    override fun areItemsTheSame(oldItem: PastWorkout, newItem: PastWorkout): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PastWorkout, newItem: PastWorkout): Boolean {
        return oldItem == newItem
    }
}