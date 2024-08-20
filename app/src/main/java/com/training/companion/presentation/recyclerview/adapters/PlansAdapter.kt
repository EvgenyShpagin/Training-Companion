package com.training.companion.presentation.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.training.companion.R
import com.training.companion.databinding.ListItemPlanBinding
import com.training.companion.domain.models.PlanFacts
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.presentation.util.nameResource
import com.training.companion.presentation.util.primaryLocale
import com.training.companion.presentation.util.thumbnailResource

class PlansAdapter(
    private val onPlanClickListener: OnClickListener,
) : ListAdapter<WorkoutPlan, PlansAdapter.PlanViewHolder>(PlanDiffUtilCallback()) {

    inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ListItemPlanBinding.bind(view)

        init {
            binding.root.setOnClickListener {
                onPlanClickListener.onClick(it, getItem(adapterPosition).id)
            }
        }

        fun bind(plan: WorkoutPlan) {
            ViewCompat.setTransitionName(binding.root, adapterPosition.toString())
            binding.trainingTypeIcon.setImageResource(plan.workoutType.thumbnailResource)
            binding.planName.text = plan.name
            binding.supportText.text = getWorkoutTypeAndDateText(plan)
            setupFactItems(plan.facts)
        }

        private fun getWorkoutTypeAndDateText(plan: WorkoutPlan): String {
            val typeName = itemView.context.getString(plan.workoutType.nameResource)
            val date = plan.creationDate!!.getFormattedDate(itemView.resources.primaryLocale)
            return "$typeName • $date"
        }

        private fun setupFactItems(facts: PlanFacts) = with(binding) {
            setFactBodyPartsCountFact(factItem1, facts)
            setFactSetsCount(factItem2, facts)
            setFactExerciseCount(factItem3, facts)
            setFactUsedTimeCount(factItem4, facts)
            setFactNoEquipment(factItem5, facts)
        }

        private fun setFactBodyPartsCountFact(factTextView: TextView, facts: PlanFacts) {
            setFactTextAndShowView(
                factTextView, factTextView.resources.getQuantityString(
                    R.plurals.body_part_count,
                    facts.includedBodyPartsCount,
                    facts.includedBodyPartsCount
                )
            )
        }

        private fun setFactSetsCount(factTextView: TextView, facts: PlanFacts) {
            setFactTextAndShowView(
                factTextView, factTextView.resources.getQuantityString(
                    R.plurals.sets_count, facts.totalSetsCount, facts.totalSetsCount
                )
            )
        }

        private fun setFactExerciseCount(factTextView: TextView, facts: PlanFacts) {
            setFactTextAndShowView(
                factTextView, factTextView.resources.getQuantityString(
                    R.plurals.exercise_count, facts.exerciseCount, facts.exerciseCount
                )
            )
        }

        private fun setFactUsedTimeCount(factTextView: TextView, facts: PlanFacts) {
            if (facts.planUsedTimes == 0) return
            setFactTextAndShowView(
                factTextView, factTextView.resources.getQuantityString(
                    R.plurals.fact_item_used_times, facts.planUsedTimes, facts.planUsedTimes
                )
            )
        }

        private fun setFactNoEquipment(factTextView: TextView, facts: PlanFacts) {
            if (facts.equipmentIsUsed) return
            setFactTextAndShowView(
                factTextView, factTextView.resources.getString(R.string.fact_item_no_equipment)
            )
        }

        private fun setFactTextAndShowView(factTextView: TextView, text: String) {
            factTextView.text = text
            factTextView.isVisible = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun interface OnClickListener {
        fun onClick(view: View, planId: Int)
    }
}

private class PlanDiffUtilCallback : DiffUtil.ItemCallback<WorkoutPlan>() {
    override fun areItemsTheSame(oldItem: WorkoutPlan, newItem: WorkoutPlan): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: WorkoutPlan, newItem: WorkoutPlan): Boolean {
        return oldItem == newItem
    }
}