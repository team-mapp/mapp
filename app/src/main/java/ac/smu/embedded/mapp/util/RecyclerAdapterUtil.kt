package ac.smu.embedded.mapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

data class TypedItem<T>(val type: Int, val item: T)

fun <T> recyclerAdapter(
    @LayoutRes viewHolderLayout: Int,
    initItems: MutableList<T> = mutableListOf(),
    initializer: (View, T) -> Unit
): BaseRecyclerAdapter<T> = BaseRecyclerAdapter(viewHolderLayout, initItems, initializer)

fun <T : TypedItem<*>> recyclerAdapter(
    viewHolderLayouts: Map<Int, Int>,
    initItems: MutableList<T> = mutableListOf(),
    initializer: (View, T) -> Unit
): BaseRecyclerAdapter<T> = BaseRecyclerAdapter(viewHolderLayouts, initItems, initializer)

class BaseRecyclerAdapter<T>(
    private val viewHolderLayouts: Map<Int, Int>,
    private val items: MutableList<T>,
    private val initializer: (View, T) -> Unit
) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder>() {

    constructor(
        viewHolderLayout: Int,
        items: MutableList<T>,
        initializer: (View, T) -> Unit
    ) : this(
        mapOf(SINGLE_TYPE_ITEM to viewHolderLayout),
        items,
        initializer
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutId = viewHolderLayouts[viewType]

        if (layoutId != null) {
            return BaseViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    layoutId,
                    parent,
                    false
                )
            )
        } else {
            throw IllegalArgumentException("Layout id not mapped")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        initializer(holder.itemView, items[position])
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is TypedItem<*>) {
            item.type
        } else {
            SINGLE_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(newItem: T) {
        items.add(newItem)
        notifyItemInserted(items.size - 1)
    }

    fun addItems(newItems: List<T>) {
        val prevItemsSize = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(prevItemsSize - 1, newItems.size)
    }

    fun replaceItems(newItems: List<T>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val SINGLE_TYPE_ITEM = -1
    }
}
