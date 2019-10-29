package ac.smu.embedded.mapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

data class TypedItem<T>(val type: Int, val item: T)

/**
 * [RecyclerView.Adapter]의 아이템들을 모두 같은 레이아웃으로 생성하는 [RecyclerView.Adapter] 생성 함수입니다.
 *
 * - 예제
 * ```kotlin
 * val adapter = recyclerAdapter(R.layout.item_test, mutableListOf("Hello")) {
 *   view, value -> view.tv_test.text = value
 * }
 *
 * 혹은
 *
 * val adapter = recyclerAdapter<String>(R.layout.item_test, mutableListOf("Hello")) {
 *   view, value -> view.tv_test.text = value
 * }
 * ```
 *
 * @param viewHolderLayout 사용할 레이아웃
 * @param initItems 어댑터의 초기 아이템
 * @param initializer 어댑터 내 ViewHolder을 초기화하기 위한 함수
 * @return 함수를 통해 생성된 [BaseRecyclerAdapter]
 */
fun <T> recyclerAdapter(
    @LayoutRes viewHolderLayout: Int,
    initItems: MutableList<T> = mutableListOf(),
    initializer: (View, T) -> Unit
): BaseRecyclerAdapter<T> = BaseRecyclerAdapter(viewHolderLayout, initItems, initializer)

/**
 * [RecyclerView.Adapter]의 아이템들을 각자 다른 레이아웃으로 생성하는 [RecyclerView.Adapter] 생성 함수입니다.
 * 이때 사용가능한 데이터는 [TypedItem<T>] 클래스를 사용해야만 합니다.
 * 각기 다른 레이아웃에 대한 초기화는 [TypedItem.type]을 통해 구분지을 수 있습니다.
 *
 * - 예제
 * ```kotlin
 * val typeHeader = 100
 * val typeItem = 101
 *
 * val adapter = recyclerAdapter(
 *  mapOf(
 *    typeHeader to R.layout.item_header,
 *    typeItem to R.layout.item_test
 *  ), mutableListOf(
 *    TypedItem(typeHeader, "Header"),
 *    TypedItem(typeItem, "Test") // TypedItem<String>으로 캐스팅됩니다.
 *  )
 * ) { view, item ->
 *  if (item.type == typeHeader)
 *    view.tv_header.text = item.item
 *  else if (item.type == typeItem)
 *    view.tv_title.text = item.item
 * }
 * ```
 *
 * @param viewHolderLayouts 사용할 레이아웃을 타입 상수와 정의한 [Map]
 * @param initItems 어댑터의 초기 아이템
 * @param initializer 어댑터 내 ViewHolder을 초기화하기 위한 함수
 * @return 함수를 통해 생성된 [BaseRecyclerAdapter]
 */
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
