package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.common.view.ContentView
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.restaurantDetail.RestaurantDetailActivity
import ac.smu.embedded.mapp.util.*
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity(R.layout.activity_profile) {

    private lateinit var listAdapter: BaseRecyclerAdapter<Restaurant>

    private val userViewModel: UserViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupObservers()
        loadContents()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }

        listAdapter = recyclerAdapter(R.layout.item_content) { _, view, value ->
            val contentView = view as ContentView
            with(value) {
                contentView.setContent(image, name)
                contentView.setOnClickListener {
                    val intent =
                        Intent(this@ProfileActivity, RestaurantDetailActivity::class.java).apply {
                            putExtra(EXTRA_DOCUMENT_ID, documentId)
                        }
                    startActivity(intent)
                }
            }
        }

        favorites_view.adapter = listAdapter
        favorites_view.layoutManager = LinearLayoutManager(this)

        iv_profile.setColorFilter(getColor(R.color.profile_color))
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getUser()
    }

    private fun setupObservers() {
        profileViewModel.favorites.observe(this, Observer {
            if (it != null) {
                profileViewModel.loadFavoriteRestaurants(it)
                tv_empty_items.visibility = View.GONE
            } else {
                tv_empty_items.visibility = View.VISIBLE
                loading_progress.visibility = View.GONE
            }
        })

        profileViewModel.favoriteRestaurants.observe(this, Observer {
            listAdapter.submitList(it)
            loading_progress.visibility = View.GONE
        })

        userViewModel.userData.observe(this, Observer {
            if (it != null) {
                with(it) {
                    val externalProfile = isExternalProfile()
                    val requestOptions = listOf(RequestOptions.circleCropTransform())
                    if (profileImage != null) {
                        iv_profile.clearColorFilter()
                        if (externalProfile) {
                            iv_profile.load(profileImage, requestOptions)
                        } else {
                            iv_profile.loadStorage(profileImage, requestOptions)
                        }
                    }
                    tv_username.text = displayName
                }
            }
        })
    }

    private fun loadContents() {
        profileViewModel.loadFavorites()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
            R.id.action_edit -> {
                val intent = Intent(this, ProfileSettingActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}