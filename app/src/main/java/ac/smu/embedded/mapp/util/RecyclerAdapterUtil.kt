package ac.smu.embedded.mapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class TypedItem<T>(val type: Int, val item: T)

/**
 * 아이템들을 모두 같은 레이아웃으로 생성하는 [ListAdapter] 생성 함수입니다.
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
 * @param diffCallback 리스트 내 일부 결과만 변경되었을때 변경된 아이템만 업데이트할 수 있도록 콜백을 정의
 * @param initializer 어댑터 내 ViewHolder을 초기화하기 위한 함수
 * @return 함수를 통해 생성된 [BaseRecyclerAdapter]
 */
fun <T> recyclerAdapter(
    @LayoutRes viewHolderLayout: Int,
    initItems: MutableList<T> = mutableListOf(),
    diffCallback: DiffUtil.ItemCallback<T> = BaseRecyclerAdapter.BaseDiffCallback(),
    initializer: (View, T) -> Unit
): BaseRecyclerAdapter<T> =
    BaseRecyclerAdapter(viewHolderLayout, initItems, diffCallback, initializer)

/**
 * 아이템들을 각자 다른 레이아웃으로 생성하는 [ListAdapter] 생성 함수입니다.
 * 이때 사용가능한 데이터는 [TypedItem] 클래스를 사용해야만 합니다.
 * 각기 다른 레이아웃에 대한 초기화는 [TypedItem.type]을 통해 구분할 수 있습니다.
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
 * @param diffCallback 리스트 내 일부 결과만 변경되었을때 변경된 아이템만 업데이트할 수 있도록 콜백을 정의
 * @param initializer 어댑터 내 ViewHolder을 초기화하기 위한 함수
 * @return 함수를 통해 생성된 [BaseRecyclerAdapter]
 */
fun <T : TypedItem<*>> recyclerAdapter(
    viewHolderLayouts: Map<Int, Int>,
    initItems: MutableList<T> = mutableListOf(),
    diffCallback: DiffUtil.ItemCallback<T> = BaseRecyclerAdapter.BaseDiffCallback(),
    initializer: (View, T) -> Unit
): BaseRecyclerAdapter<T> =
    BaseRecyclerAdapter(viewHolderLayouts, initItems, diffCallback, initializer)

class BaseRecyclerAdapter<T>(
    private val viewHolderLayouts: Map<Int, Int>,
    initItems: MutableList<T>,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val initializer: (View, T) -> Unit
) : ListAdapter<T, BaseRecyclerAdapter.BaseViewHolder>(diffCallback) {

    constructor(
        viewHolderLayout: Int,
        initItems: MutableList<T>,
        diffCallback: DiffUtil.ItemCallback<T>,
        initializer: (View, T) -> Unit
    ) : this(
        mapOf(TYPE_SINGLE_ONLY to viewHolderLayout),
        initItems,
        diffCallback,
        initializer
    )

    init {
        if (initItems.isNotEmpty()) {
            submitList(initItems)
        }
    }

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
        initializer(holder.itemView, getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is TypedItem<*>) {
            item.type
        } else {
            TYPE_SINGLE_ONLY
        }
    }

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class BaseDiffCallback<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = false
    }

    companion object {
        private const val TYPE_SINGLE_ONLY = -1
    }
}