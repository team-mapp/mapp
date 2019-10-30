package ac.smu.embedded.mapp.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val marginLeft: Int = 0,
    private val marginRight: Int = 0,
    private val marginTop: Int = 0,
    private val marginBottom: Int = 0
) : RecyclerView.ItemDecoration() {
    constructor(margin: Int) : this(margin, margin, margin, margin)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = marginTop
            }
            left = marginLeft
            right = marginRight
            bottom = marginBottom
        }
    }
}