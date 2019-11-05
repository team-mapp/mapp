package ac.smu.embedded.mapp.search

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class AutoCompleteAdapter(
    context: Context,
    resId: Int,
    private val items: MutableList<String> = mutableListOf()
) : ArrayAdapter<String>(context, resId, items) {

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                val filteredItem = items.filter { it.contains(constraint) }
                FilterResults().apply {
                    values = filteredItem
                    count = filteredItem.size
                }
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
    
    override fun getFilter(): Filter {
        return filter
    }

    fun updateItems(items: List<String>) {
        clear()
        addAll(items.toMutableList())
    }
}