package com.training.companion.presentation.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.training.companion.R
import com.training.companion.databinding.ListItemExerciseIconBinding
import com.training.companion.domain.models.IconExercise
import com.training.companion.presentation.util.hideKeyboard


class ExerciseSelectionAdapter(
    private val externalOnSelectListener: (exercise: IconExercise) -> Unit,
    private val inputMethodManager: InputMethodManager
) : ListAdapter<IconExercise, ExerciseSelectionAdapter.ExerciseViewHolder>(ExerciseDiffUtil()) {

    private var positionOfSelected = RecyclerView.NO_POSITION

    private var currentChosenExerciseId: Int? = null

    override fun onCurrentListChanged(
        previousList: MutableList<IconExercise>,
        currentList: MutableList<IconExercise>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        refreshCurrentSelectedItem()
    }

    fun setChosenExerciseId(exerciseId: Int) {
        currentChosenExerciseId = exerciseId
        refreshCurrentSelectedItem()
    }

    private fun refreshCurrentSelectedItem() {
        if (currentChosenExerciseId == null) return

        val foundItemPosition = currentList.indexOfFirst { currentChosenExerciseId == it.id }

        if (foundItemPosition == -1) {
            positionOfSelected = RecyclerView.NO_POSITION
            return
        } else {
            positionOfSelected = foundItemPosition
            notifyItemChanged(foundItemPosition)
        }

    }

    inner class ExerciseViewHolder(view: View) : ViewHolder(view) {
        private val binding = ListItemExerciseIconBinding.bind(view)

        init {
            binding.root.setOnClickListener {
                val itemPosition = adapterPosition
                if (positionOfSelected != itemPosition) {
                    if (positionOfSelected != RecyclerView.NO_POSITION) {
                        notifyItemChanged(positionOfSelected)
                    }
                    val exercise = currentList[itemPosition]
                    externalOnSelectListener.invoke(exercise)
                    view.hideKeyboard(inputMethodManager)
                    positionOfSelected = itemPosition
                    currentChosenExerciseId = exercise.id
                    notifyItemChanged(itemPosition)
                }
            }
        }

        fun bind(exercise: IconExercise) {
            binding.also {
                it.exercise = exercise
                it.executePendingBindings()
            }
            binding.root.isSelected = adapterPosition == positionOfSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            R.layout.list_item_exercise_icon,
            parent,
            false
        )
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    private class ExerciseDiffUtil : DiffUtil.ItemCallback<IconExercise>() {
        override fun areItemsTheSame(
            oldItem: IconExercise,
            newItem: IconExercise
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: IconExercise,
            newItem: IconExercise
        ): Boolean {
            return false
        }
    }
}