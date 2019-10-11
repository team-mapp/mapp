package ac.smu.embedded.findingtaste.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

fun <T> recyclerAdapter(
    @LayoutRes viewHolderLayout: Int,
    initItems: MutableList<T> = mutableListOf(),
    initializer: (View, T) -> Unit
): BaseRecyclerAdapter<T> = BaseRecyclerAdapter(viewHolderLayout, initItems, initializer)

class BaseRecyclerAdapter<T>(
    @LayoutRes private val viewHolderLayout: Int,
    private val items: MutableList<T>,
    private val initializer: (View, T) -> Unit
) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(LayoutInflater.from(parent.context).inflate(viewHolderLayout, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        initializer(holder.itemView, items[position])
    }

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
}

