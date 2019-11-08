package ac.smu.embedded.mapp.detail

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.view.ContentView
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.profile.ProfileActivity
import ac.smu.embedded.mapp.restaurantDetail.RestaurantDetailActivity
import ac.smu.embedded.mapp.search.SearchActivity
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.MarginItemDecoration
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity(R.layout.activity_detail) {

    private val detailViewModel: DetailViewModel by viewModel()

    private lateinit var adapter: BaseRecyclerAdapter<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(EXTRA_DATA_TYPE) && intent.hasExtra(EXTRA_DOCUMENT_ID)) {
            val dataType = intent.getIntExtra(EXTRA_DATA_TYPE, 0)
            val documentId = intent.getStringExtra(EXTRA_DOCUMENT_ID)!!

            initView()
            loadContents(dataType, documentId)
        } else {
            throw UnsupportedOperationException(
                "Not received intent data, required EXTRA_DATA_TYPE, EXTRA_DOCUMENT_ID"
            )
        }
    }

    private fun initView() {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        adapter =
            recyclerAdapter(
                R.layout.item_content
            ) { _, view, value ->
                val contentView = view as ContentView
                contentView.setContent(
                    value.image, value.name,
                    isFavorite = false,
                    visibleFavorite = true
                )
                contentView.setOnClickListener {
                    navigateRestaurantDetail(value.documentId)
                }
            }

        val marginDimen = resources.getDimension(R.dimen.keyline_small).toInt()

        related_content_view.layoutManager = LinearLayoutManager(this)
        related_content_view.addItemDecoration(MarginItemDecoration(marginBottom = marginDimen))
        related_content_view.adapter = adapter

        search_bar_layout.setOnClickListener { navigateSearch() }
    }


    private fun loadContents(dataType: Int, documentId: String) {
        val restaurantObserver = createRestaurantObserver()
        if (dataType == TYPE_CELEB) {
            detailViewModel.loadCeleb(documentId).observe(this, Observer { resource ->
                resource.onSuccess { updateProfile(it?.name!!, it.image) }
            })
            detailViewModel.loadCelebRestaurants(documentId).observe(this, restaurantObserver)
        } else if (dataType == TYPE_PROGRAM) {
            detailViewModel.loadProgram(documentId).observe(this, Observer { resource ->
                resource.onSuccess { updateProfile(it?.name!!, it.image) }
            })
            detailViewModel.loadProgramRestaurants(documentId).observe(this, restaurantObserver)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateProfile(name: String, image: String) {
        view_profile.setName(name)
        view_profile.setImage(image, true)
    }

    private fun createRestaurantObserver(): Observer<Resource<List<Restaurant>>> =
        Observer { resource ->
            resource.onSuccess {
                loading_progress.visibility = View.GONE
                adapter.submitList(it!!)
            }
        }

    private fun navigateSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(
            intent,
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                search_bar_layout,
                "search_bar"
            ).toBundle()
        )
    }

    private fun navigateRestaurantDetail(documentId: String) {
        val intent = Intent(this, RestaurantDetailActivity::class.java)
        intent.putExtra("document_id", documentId)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_my -> startActivity(Intent(this, ProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_DATA_TYPE = "data_type"
        const val EXTRA_DOCUMENT_ID = "document_id"

        const val TYPE_CELEB = 0x100
        const val TYPE_PROGRAM = 0x101
    }
}