package ac.smu.embedded.mapp.search

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.detail.DetailActivity
import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.util.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.item_content_card.view.*

class SearchActivity : AppCompatActivity(R.layout.activity_search) {

    private val viewModel by viewModels<SearchViewModel> { getViewModelFactory() }
    private lateinit var adapter: BaseRecyclerAdapter<TypedItem<out Any>>
    private lateinit var autocompleteAdapter: AutoCompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        viewModel.searchResults.observe(this, Observer {
            if (it != null) {
                loading_progress.visibility = View.GONE

                val items = mutableListOf<TypedItem<Any>>()
                if (it.celebsResult != null) {
                    items.add(
                        TypedItem(
                            typeHeader,
                            "${getString(R.string.subtitle_celebs)} (${it.celebsResult.size})"
                        )
                    )
                    it.celebsResult.forEach { celeb ->
                        items.add(TypedItem(typeCeleb, celeb))
                    }
                }
                if (it.programsResult != null) {
                    items.add(
                        TypedItem(
                            typeHeader,
                            "${getString(R.string.subtitle_programs)} (${it.programsResult.size})"
                        )
                    )
                    it.programsResult.forEach { celeb ->
                        items.add(TypedItem(typeProgram, celeb))
                    }
                }
                if (it.restaurantResult != null) {
                    items.add(
                        TypedItem(
                            typeHeader,
                            "${getString(R.string.subtitle_restaurants)} (${it.restaurantResult.size})"
                        )
                    )
                    it.restaurantResult.forEach { celeb ->
                        items.add(TypedItem(typeRestaurant, celeb))
                    }
                }
                adapter.replaceItems(items)
            } else {
                loading_progress.visibility = View.VISIBLE
            }
        })

        viewModel.searchAutoComplete.observe(this, Observer {
            if (it != null) {
                val autoCompleteItems = mutableListOf<String>()
                if (it.celebsResult != null) {
                    it.celebsResult.forEach { celeb -> autoCompleteItems.add(celeb.name) }
                }
                if (it.programsResult != null) {
                    it.programsResult.forEach { program -> autoCompleteItems.add(program.name) }
                }
                if (it.restaurantResult != null) {
                    it.restaurantResult.forEach { restaurant -> autoCompleteItems.add(restaurant.name) }
                }
                if (autoCompleteItems.size > 0) {
                    autocompleteAdapter.updateItems(autoCompleteItems)
                }
            }
        })
    }

    private fun initView() {
        search_bar_layout.transitionName = "search_bar"

        adapter = recyclerAdapter(
            mapOf(
                typeHeader to R.layout.item_header,
                typeCeleb to R.layout.item_content_card,
                typeProgram to R.layout.item_content_card,
                typeRestaurant to R.layout.item_content_card
            ),
            mutableListOf()
        ) { view, value ->
            when (value.type) {
                typeHeader -> {
                    (view as TextView).text = value.item as String
                }
                typeCeleb -> {
                    val celeb = value.item as Celeb
                    view.iv_content.load(this, celeb.image)
                    view.tv_content.text = celeb.name
                    view.setOnClickListener {
                        navigateDetail(DetailActivity.TYPE_CELEB, celeb.documentId)
                    }
                }
                typeProgram -> {
                    val program = value.item as Program
                    view.iv_content.load(this, program.image)
                    view.tv_content.text = program.name
                    view.setOnClickListener {
                        navigateDetail(DetailActivity.TYPE_PROGRAM, program.documentId)
                    }
                }
                typeRestaurant -> {
                    val restaurant = value.item as Restaurant
                    view.iv_content.load(this, restaurant.image)
                    view.tv_content.text = restaurant.name
                }
            }
        }

        val marginDimen = resources.getDimension(R.dimen.keyline_small).toInt()
        search_content_view.layoutManager = LinearLayoutManager(this)
        search_content_view.addItemDecoration(MarginItemDecoration(marginBottom = marginDimen))
        search_content_view.adapter = adapter

        autocompleteAdapter = AutoCompleteAdapter(this, R.layout.item_simple_text)

        edit_search.setAdapter(autocompleteAdapter)
        edit_search.addTextChangedListener {
            viewModel.search(edit_search.text.toString().trim(), true)
        }

        edit_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.search(v.text.toString().trim())
            }
            return@setOnEditorActionListener true
        }

        edit_search.setOnItemClickListener { _, _, _, _ ->
            viewModel.search(edit_search.text.toString().trim())
        }

        btn_search.setOnClickListener { viewModel.search(edit_search.text.toString().trim()) }

        edit_search.requestFocusFromTouch()
    }

    private fun navigateDetail(type: Int, documentId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA_TYPE, type)
        intent.putExtra(DetailActivity.EXTRA_DOCUMENT_ID, documentId)
        startActivity(intent)
    }

    companion object {
        private const val typeHeader = 0
        private const val typeCeleb = 1
        private const val typeProgram = 2
        private const val typeRestaurant = 3
    }
}