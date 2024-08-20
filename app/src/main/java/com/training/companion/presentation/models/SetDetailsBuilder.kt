package com.training.companion.presentation.models

import android.os.Parcel
import android.os.Parcelable
import com.training.companion.domain.enums.PlanEditType
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Equipment


sealed class SetDetailsBuilder(
    open val workoutType: WorkoutType,
    open val equipment: List<Equipment>?,
) : Parcelable {
    data class OfPlanEdit(
        val planEditType: PlanEditType,
        val planId: Int,
        override val workoutType: WorkoutType,
        override val equipment: List<Equipment>? = null,
        val exerciseOrdinal: Int? = null,
    ) : SetDetailsBuilder(workoutType, equipment) {

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(exerciseOrdinal)
            parcel.writeInt(planId)
        }

        override fun describeContents() = 0


        companion object CREATOR : Parcelable.Creator<OfPlanEdit> {
            override fun createFromParcel(parcel: Parcel) = OfPlanEdit(
                planEditType = PlanEditType.entries[parcel.readInt()],
                planId = parcel.readInt(),
                workoutType = WorkoutType.entries[parcel.readInt()],
                equipment = getEquipmentFromParcel(parcel),
                exerciseOrdinal = parcel.readValue(Int::class.java.classLoader) as? Int
            )

            override fun newArray(size: Int) = arrayOfNulls<OfPlanEdit?>(size)
        }
    }

    data class OfWorkoutSet(
        override val workoutType: WorkoutType,
        override val equipment: List<Equipment>? = null,
        val exerciseId: Int? = null,
    ) : SetDetailsBuilder(workoutType, equipment) {
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(exerciseId)
            writeEquipmentToParcel(parcel, equipment)
            parcel.writeValue(workoutType.ordinal)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<OfWorkoutSet> {
            override fun createFromParcel(parcel: Parcel) = OfWorkoutSet(
                workoutType = WorkoutType.entries[parcel.readInt()],
                equipment = getEquipmentFromParcel(parcel),
                exerciseId = parcel.readValue(Int::class.java.classLoader) as? Int
            )

            override fun newArray(size: Int) = arrayOfNulls<OfWorkoutSet?>(size)
        }
    }

    protected companion object {
        fun getEquipmentFromParcel(parcel: Parcel): List<Equipment>? {
            val idsOfEquipment = parcel.createIntArray()
            val namesOfEquipment = parcel.createStringArray()
            return idsOfEquipment?.let { ids ->
                List(ids.count()) { i ->
                    Equipment(ids[i], namesOfEquipment!![i])
                }
            }
        }

        fun writeEquipmentToParcel(parcel: Parcel, equipment: List<Equipment>?) {
            parcel.writeIntArray(equipment?.let { IntArray(it.count()) { i -> it[i].id } })
            parcel.writeStringArray(equipment?.let { Array(it.count()) { i -> it[i].name } })
        }
    }
}