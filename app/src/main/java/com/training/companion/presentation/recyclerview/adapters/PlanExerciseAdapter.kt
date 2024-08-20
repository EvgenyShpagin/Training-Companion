package com.training.companion.presentation.recyclerview.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.training.companion.R
import com.training.companion.data.repositories.AppSettings
import com.training.companion.databinding.ListItemCreatePlanExerciseBinding
import com.training.companion.domain.enums.PlanExerciseListState
import com.training.companion.domain.enums.WeightUnit
import com.training.companion.domain.models.PlanExercise
import com.training.companion.domain.models.Reps
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.Weight
import com.training.companion.presentation.util.decimalFormat0_0


class PlanExerciseAdapter(
    private val context: Context,
    private val onItemClickListener: ((view: View, exerciseOrdinal: Int) -> Unit)?,
) : ListAdapter<PlanExercise, PlanExerciseAdapter.PlanExerciseViewHolder>(
    CreatePlanExerciseDiffUtil()
) {

    private var currentState = PlanExerciseListState.Default
    private val weightUnits = AppSettings.units.weightUnit

    fun onStateChange(state: PlanExerciseListState) {
        currentState = state
        notifyItemRangeChanged(0, currentList.size)
    }

    private val parametersStrings = object {

        val primaryTextColor = context.getColor(R.color.plan_exercise_param_text_primary)
        val secondaryTextColor = context.getColor(R.color.plan_exercise_param_text_secondary)

        val auxiliary = context.resources.getStringArray(R.array.auxiliary_param_text)

        fun getForSets(setsNumber: Int) = context.resources.getQuantityString(
            R.plurals.create_plan_param_sets,
            setsNumber,
            setsNumber
        )

        fun getForReps(reps: Reps) = context.resources.getQuantityString(
            R.plurals.create_plan_param_reps,
            if (repIsOne(reps)) 1 else 2,
            if (reps is Reps.Max) {
                context.getString(R.string.plan_reps_max)
            } else {
                reps
            }
        )


        fun getForWeight(weight: Weight): String {
            val valueWithUnits = context.getString(
                if (weightUnits == WeightUnit.Kilograms) {
                    R.string.weight_kg_suffix
                } else {
                    R.string.weight_lb_suffix
                }, decimalFormat0_0.format(weight.convert(weightUnits).getRoundedTo100sValue())
            )
            return context.getString(R.string.create_plan_param_weight, valueWithUnits)
        }

        fun getForDuration(duration: Time) =
            context.getString(
                R.string.create_plan_param_duration,
                duration.toString(Time.Format.MM_SS)
            )


        fun getForRestTime(restTime: Time) =
            context.getString(R.string.create_plan_param_rest, restTime.toString(Time.Format.MM_SS))

        private fun repIsOne(reps: Reps) = when (reps) {
            is Reps.Exact -> reps.value == 1
            else -> false
        }
    }

    inner class PlanExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ListItemCreatePlanExerciseBinding.bind(view)

        init {

            onItemClickListener?.let { listener ->
                binding.removeButton.setOnClickListener {
                    listener.invoke(it, layoutPosition)
                }

                binding.removeButton.setOnClickListener {
                    listener.invoke(it, layoutPosition)
                }

                binding.root.setOnClickListener {
                    if (currentState != PlanExerciseListState.Deleting) {
                        listener.invoke(it, layoutPosition)
                    }
                }
            }
        }

        fun bind(planExercise: PlanExercise) {
            binding.iconFilenameString = planExercise.set.exercise!!.iconFilename
            binding.nameString = planExercise.set.exercise.name
            val paramsString = getParametersText(planExercise)
            val paramsSpannableString = getUpdatedSpannableParamsText(paramsString)
            binding.paramsCharSequence = paramsSpannableString
            binding.currentState = currentState
            binding.executePendingBindings()
        }

        private fun getUpdatedSpannableParamsText(fullParamsText: String): SpannableString {
            val spannableString = SpannableString(fullParamsText)
            spannableString.setSpan(
                ForegroundColorSpan(parametersStrings.primaryTextColor),
                0,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            parametersStrings.auxiliary.forEach {
                val substringStartIndex = fullParamsText.indexOf(it)
                if (substringStartIndex != -1) {
                    val substringEndIndex = substringStartIndex + it.length
                    spannableString.setSpan(
                        ForegroundColorSpan(parametersStrings.secondaryTextColor),
                        substringStartIndex,
                        substringEndIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannableString
        }

        private fun getParametersText(planExercise: PlanExercise): String {
            val stringBuilder = StringBuilder(100)
            stringBuilder.append(parametersStrings.getForSets(planExercise.setsNumber))
            if (planExercise.set.reps != null) {
                stringBuilder.append(parametersStrings.getForReps(planExercise.set.reps))
            }
            if (planExercise.set.weight != null) {
                stringBuilder.append(parametersStrings.getForWeight(planExercise.set.weight))
            }
            if (planExercise.set.duration != null) {
                stringBuilder.append('\n')
                stringBuilder.append(parametersStrings.getForDuration(planExercise.set.duration))
            }
            if (planExercise.set.restTime != null) {
                if (planExercise.set.duration == null) {
                    stringBuilder.append('\n')
                } else {
                    stringBuilder.append(' ')
                }
                stringBuilder.append(parametersStrings.getForRestTime(planExercise.set.restTime))
            }
            return stringBuilder.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanExerciseViewHolder {
        val itemLayoutInflater = LayoutInflater.from(parent.context)
        val view = itemLayoutInflater.inflate(
            R.layout.list_item_create_plan_exercise, parent, false
        )
        return PlanExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanExerciseViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}

private class CreatePlanExerciseDiffUtil : DiffUtil.ItemCallback<PlanExercise>() {
    override fun areItemsTheSame(
        oldItem: PlanExercise, newItem: PlanExercise,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PlanExercise, newItem: PlanExercise,
    ): Boolean {
        return oldItem == newItem
    }

}