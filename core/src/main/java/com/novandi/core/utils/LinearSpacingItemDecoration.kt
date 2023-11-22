package com.novandi.core.utils

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

class LinearSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: android.view.View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        outRect.top = if (parent.getChildAdapterPosition(view) == 0) space else 0
    }
}