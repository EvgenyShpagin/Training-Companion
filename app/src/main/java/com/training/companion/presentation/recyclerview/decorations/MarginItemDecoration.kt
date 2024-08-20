package com.training.companion.presentation.recyclerview.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val verticalSpace: Int = 0,
    private val horizontalSpace: Int = 0,
    private val withTopItemMargin: Boolean = false,
) : RecyclerView.ItemDecoration() {

    constructor(
        verticalSpace: Float = 0f,
        horizontalSpace: Float = 0f,
        withTopItemMargin: Boolean = false,
    ) : this(
        verticalSpace = verticalSpace.toInt(),
        horizontalSpace = horizontalSpace.toInt(),
        withTopItemMargin = withTopItemMargin
    )

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = if (withTopItemMargin) {
                    verticalSpace
                } else {
                    0
                }
            }
            left = horizontalSpace
            right = horizontalSpace
            bottom = verticalSpace
        }
    }
}