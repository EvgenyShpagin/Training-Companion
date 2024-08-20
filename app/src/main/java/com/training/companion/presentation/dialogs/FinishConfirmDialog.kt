package com.training.companion.presentation.dialogs

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.training.companion.R

class FinishConfirmDialog(private val context: Context) {
    fun show(onConfirm: DialogInterface.OnClickListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.finish_workout_dialog_title)
            .setMessage(R.string.finish_workout_dialog_message)
            .setPositiveButton(R.string.submit, onConfirm)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}