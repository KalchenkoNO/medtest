package com.ce2af4a3.tests.presentation

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecoration(
    private val offset: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        when (parent.getChildAdapterPosition(view)) {
            0 -> outRect.set(offset, offset, offset, offset)
            else -> outRect.set(offset, 0, offset, offset)
        }
    }
}