package ac.smu.embedded.mapp.detail

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.search.SearchActivity
import ac.smu.embedded.mapp.util.*
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_content_card.view.*

class DetailActivity : AppCompatActivity(R.layout.activity_detail) {

    private val viewModel by viewModels<DetailViewModel> { getViewModelFactory() }

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
        supportActionBar?.apply {
            elevation = 0.0f
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(createHomeAsUpIndicator())
        }

        adapter =
            recyclerAdapter(
                R.layout.item_content_card,
                mutableListOf()
            ) { view, value ->
                view.btn_favorite.visibility = View.VISIBLE
                view.iv_content.load(this, value.image)
                view.tv_content.text = value.name
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
            viewModel.loadCeleb(documentId).observe(this, Observer { resource ->
                resource.onSuccess { updateProfile(it?.name!!, it.image) }
            })
            viewModel.loadCelebRestaurants(documentId).observe(this, restaurantObserver)
        } else if (dataType == TYPE_PROGRAM) {
            viewModel.loadProgram(documentId).observe(this, Observer { resource ->
                resource.onSuccess { updateProfile(it?.name!!, it.image) }
            })
            viewModel.loadProgramRestaurants(documentId).observe(this, restaurantObserver)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateProfile(name: String, image: String) {
        val requestListener = createRequestListener()
        iv_profile.loadBitmap(
            this,
            image,
            RequestOptions.circleCropTransform(),
            requestListener
        )
        tv_name.text = "#$name"
    }

    private fun createRestaurantObserver(): Observer<Resource<List<Restaurant>>> =
        Observer { resource ->
            resource.onSuccess {
                loading_progress.visibility = View.GONE
                adapter.replaceItems(it!!)
            }
        }

    @SuppressLint("PrivateResource")
    private fun createHomeAsUpIndicator(): Drawable =
        getDrawable(R.drawable.abc_ic_ab_back_material)?.apply {
            colorFilter =
                PorterDuffColorFilter(
                    getColor(R.color.colorAccent),
                    PorterDuff.Mode.SRC_ATOP
                )
        }!!

    private fun createRequestListener(): RequestListener<Bitmap> {
        return object : RequestListener<Bitmap> {
            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                if (resource != null) {
                    val palette = Palette.from(resource).generate()
                    // updatePaletteColor(palette)
                }
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                supportStartPostponedEnterTransition()
                return false
            }
        }
    }

    private fun updatePaletteColor(palette: Palette) {
        val swatch = palette.mutedSwatch
        val backgroundColor = palette.getMutedColor(getColor(R.color.colorPrimary))
        if (swatch != null) {
            window.statusBarColor = ColorUtils.manipulateColor(backgroundColor, 0.32f)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(backgroundColor))
            view_profile_bg.setBackgroundColor(backgroundColor)
            tv_name.setTextColor(swatch.titleTextColor)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
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