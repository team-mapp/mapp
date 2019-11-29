package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.util.load
import ac.smu.embedded.mapp.util.loadStorage
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity(R.layout.activity_profile) {

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

        iv_profile.setColorFilter(getColor(R.color.profile_color))

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
        view_pager.adapter = ProfilePagerAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getUser()
    }

    private fun setupObservers() {
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