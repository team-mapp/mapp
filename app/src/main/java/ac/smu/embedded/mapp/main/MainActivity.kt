package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.model.Notification
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.profile.ProfileActivity
import ac.smu.embedded.mapp.restaurantDetail.RestaurantDetailActivity
import ac.smu.embedded.mapp.search.SearchActivity
import ac.smu.embedded.mapp.util.*
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_notification.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val mainViewModel: MainViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    private lateinit var notificationAdapter: BaseRecyclerAdapter<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupObservers()
        loadContents()
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getUser()
    }

    private fun initView() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                view_pager.setCurrentItem(tab?.position!!, true)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No-op
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No-op
            }

        })

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tab_layout.getTabAt(position)?.select()
            }
        })
        view_pager.adapter = MainPagerAdapter(this)

        search_bar_layout.setOnClickListener { navigateSearch() }

        notificationAdapter =
            recyclerAdapter(R.layout.item_notification) { viewHolder, _, item ->
                if (item.type == NotificationConstants.TYPE_REVIEW_CREATED) {
                    val restaurant = item.data as Restaurant
                    viewHolder.iv_content.loadStorage(
                        restaurant.image,
                        listOf(RequestOptions.circleCropTransform())
                    )
                    viewHolder.tv_title.text = String.format(
                        getString(R.string.notification_review_created_content),
                        restaurant.name
                    )
                    viewHolder.tv_subtitle.text = DateUtils.getRelativeTimeSpanString(item.notifyAt)
                    viewHolder.itemView.setOnClickListener {
                        val intent = Intent(this, RestaurantDetailActivity::class.java)
                        intent.putExtra(EXTRA_DOCUMENT_ID, item.content)
                        startActivity(intent)
                    }
                }
            }

        notification_view.layoutManager = LinearLayoutManager(this)
        notification_view.adapter = notificationAdapter
    }

    private fun setupObservers() {
        mainViewModel.notifications.observe(this, Observer {
            notification_toolbar.title = "${getString(R.string.menu_notification)} (${it.size})"
            tv_empty_items.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            notificationAdapter.submitList(it)
        })

        userViewModel.userData.observe(this, Observer {
            if (it != null) {
                if (it.profileImage != null) {
                    view_profile.setImage(it.profileImage, isStorage = !it.isExternalProfile())
                }
                view_profile.setTitle(it.displayName!!)
            }
        })
    }

    private fun loadContents() {
        mainViewModel.loadNotifications()
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_my -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.action_notification -> {
                drawer_layout.openDrawer(GravityCompat.END)
            }
        }
        return true
    }
}