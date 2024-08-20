package com.training.companion.presentation.recyclerview.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.training.companion.R
import com.training.companion.databinding.ListItemEquipmentBinding
import com.training.companion.domain.models.Equipment


private const val ALL_EQUIPMENT_ITEM_POSITION = 0
private const val ALL_EQUIPMENT_ITEM_ID = -1

class EquipmentFilterAdapter(
    private val context: Context,
    equipmentList: List<Equipment>,
    private val onEquipmentClick: (equipment: Equipment, isChecked: Boolean) -> Unit,
    whichAlreadyChecked: List<Boolean>
) : ListAdapter<Equipment, EquipmentFilterAdapter.EquipmentFilterViewHolder>(
    ExerciseFilterItemDiffUtil()
) {

    private var whichEquipmentChecked: BooleanArray = expandListBySpecialOne(whichAlreadyChecked)

    private val itemsBackgroundFor = object {
        val firstChecked = get(context, R.drawable.exercise_filter_selected_first_item_shape)
        val middleChecked = get(context, R.drawable.exercise_filter_selected_middle_item_shape)
        val lastChecked = get(context, R.drawable.exercise_filter_selected_last_item_shape)
        val first = get(context, R.drawable.exercise_filter_first_item_shape)
        val middle = get(context, R.drawable.exercise_filter_middle_item_shape)
        val last = get(context, R.drawable.exercise_filter_last_item_shape)
        private fun get(context: Context, res: Int) = ContextCompat.getDrawable(context, res)
    }

    private val itemAllEquipment = Equipment(
        ALL_EQUIPMENT_ITEM_ID,
        context.resources.getString(R.string.equipment_all_special_item)
    )

    init {
        submitList(equipmentList)
    }

    override fun submitList(list: List<Equipment>?) {
        val listWithSpecialItem = ArrayList<Equipment>(list!!.size + 1)
        listWithSpecialItem.add(itemAllEquipment)
        listWithSpecialItem.addAll(list)

        super.submitList(listWithSpecialItem)

        if (whichEquipmentChecked.size != listWithSpecialItem.size) {
            whichEquipmentChecked = BooleanArray(listWithSpecialItem.size)
        }
    }

    inner class EquipmentFilterViewHolder(view: View) : ViewHolder(view) {

        private val binding = ListItemEquipmentBinding.bind(view)

        fun bind(position: Int, equipment: Equipment) {

            binding.onClickListener = View.OnClickListener {
                val isItCheckedNow = !whichEquipmentChecked[position]
                if (position == ALL_EQUIPMENT_ITEM_POSITION) {
                    if (isItCheckedNow) {
                        markCheckedAllAndNotify()
                    } else {
                        markUncheckedAllAndNotify()
                    }
                } else {
                    if (whichEquipmentChecked[ALL_EQUIPMENT_ITEM_POSITION]) {
                        updateItemState(ALL_EQUIPMENT_ITEM_POSITION, isChecked = false)
                        notifyItemChanged(ALL_EQUIPMENT_ITEM_POSITION)
                    }
                    updateItemState(position, isItCheckedNow)
                    onEquipmentClick(equipment, isItCheckedNow)
                    if (whichEquipmentChecked.count { !it } == 1) {
                        whichEquipmentChecked[ALL_EQUIPMENT_ITEM_POSITION] = true
                        notifyItemChanged(ALL_EQUIPMENT_ITEM_POSITION)
                    }
                }
            }

            setItemUI(equipment, position)
        }

        private fun paintItem(isChecked: Boolean, position: Int) {
            binding.root.background = if (position == 0) {
                if (isChecked) itemsBackgroundFor.firstChecked
                else itemsBackgroundFor.first
            } else if (position == currentList.lastIndex) {
                if (isChecked) itemsBackgroundFor.lastChecked
                else itemsBackgroundFor.last
            } else {
                if (isChecked) itemsBackgroundFor.middleChecked
                else itemsBackgroundFor.middle
            }
        }

        private fun updateItemState(position: Int, isChecked: Boolean) {
            whichEquipmentChecked[position] = isChecked
            binding.isChecked = isChecked
            paintItem(isChecked, position)
        }

        private fun setItemUI(equipment: Equipment, position: Int) {
            binding.equipment = equipment
            binding.isChecked = whichEquipmentChecked[position]
            paintItem(whichEquipmentChecked[position], position)
            if (position == ALL_EQUIPMENT_ITEM_POSITION) {
                binding.textStyle = Typeface.BOLD
            } else {
                binding.textStyle = Typeface.NORMAL
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentFilterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_equipment, parent, false)
        return EquipmentFilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentFilterViewHolder, position: Int) {
        holder.bind(position, currentList[position])
    }

    private fun markCheckedAllAndNotify() {
        whichEquipmentChecked = BooleanArray(whichEquipmentChecked.size) { index ->
            val equipment = currentList[index]
            val equipmentId = equipment.id
            if (equipmentId != ALL_EQUIPMENT_ITEM_ID) {
                onEquipmentClick(equipment, true)
            }
            return@BooleanArray true
        }
        notifyItemRangeChanged(0, currentList.size)
    }

    private fun markUncheckedAllAndNotify() {
        whichEquipmentChecked = BooleanArray(whichEquipmentChecked.size) { index ->
            val equipment = currentList[index]
            val equipmentId = equipment.id
            if (equipmentId != ALL_EQUIPMENT_ITEM_ID) {
                onEquipmentClick(equipment, false)
            }
            return@BooleanArray false
        }
        notifyItemRangeChanged(0, currentList.size)
    }

    private fun expandListBySpecialOne(defaultList: List<Boolean>): BooleanArray {
        val result = BooleanArray(defaultList.size + 1)
        for (i in 1..result.lastIndex) {
            result[i] = defaultList[i - 1]
        }
        if (!defaultList.contains(false)) {
            result[0] = true
        }
        return result
    }

    private class ExerciseFilterItemDiffUtil : DiffUtil.ItemCallback<Equipment>() {

        override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
            return oldItem.name == newItem.name
        }
    }

}